package net.streamok.service.document.metrics

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.PeriodicOperationDefinition

import java.util.concurrent.atomic.AtomicInteger

import static net.streamok.lib.conf.Conf.configuration

class DocumentsCountMetric extends PeriodicOperationDefinition {

    DocumentsCountMetric(Vertx vertx) {
        super(vertx,
                'document.metrics.count',
                configuration().instance().getInt('DOCUMENT_METRIC_COUNT_INTERVAL', 15000))
    }

    @Override
    OperationHandler handler() {
        { fiberContext ->
            def mongo = fiberContext.dependency(MongoClient)
            mongo.getCollections {
                def total = 0
                def sem = new AtomicInteger(it.result().size())
                if(sem.intValue() > 0) {
                    it.result().each {
                        mongo.count(it, new JsonObject()) {
                            total += it.result()
                            if (sem.decrementAndGet() == 0) {
                                fiberContext.vertx().eventBus().send('metrics.put', null, new DeliveryOptions().addHeader('key', 'service.document.count').addHeader('value', total.toString()))
                            }
                        }
                    }
                } else {
                    fiberContext.vertx().eventBus().send('metrics.put', null, new DeliveryOptions().addHeader('key', 'service.document.count').addHeader('value', '0'))
                }
            }
        }
    }

}