package net.streamok.service.document.metrics

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition

import static org.slf4j.LoggerFactory.getLogger

class DocumentsCountMetric implements FiberDefinition {

    private static final LOG = getLogger(DocumentsCountMetric)

    @Override
    String address() {
        'document.metrics.count'
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