package net.streamok.service.document.metrics

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.PeriodicFiberDefinition

import static net.streamok.lib.conf.Conf.configuration

class DocumentsCountMetric extends PeriodicFiberDefinition {

    DocumentsCountMetric(Vertx vertx) {
        super(vertx,
                'document.metrics.count',
                configuration().instance().getInt('DOCUMENT_METRIC_COUNT_INTERVAL', 15000))
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def mongo = fiberContext.dependency(MongoClient)
            mongo.getCollections {
                def total = 0
                def sem = it.result().size()
                it.result().each {
                    mongo.count(it, new JsonObject()) {
                        total += it.result()
                        sem--
                        if(sem == 0) {
                            fiberContext.vertx().eventBus().send('metrics.put', null, new DeliveryOptions().addHeader('key', 'service.document.count').addHeader('value', total.toString()))
                        }
                    }
                }
            }
        }
    }

}