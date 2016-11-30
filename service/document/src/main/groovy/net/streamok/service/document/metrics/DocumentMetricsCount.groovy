package net.streamok.service.document.metrics

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.service.document.MongodbMapper
import net.streamok.service.document.QueryBuilder

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

import static com.google.common.base.MoreObjects.firstNonNull
import static java.util.concurrent.TimeUnit.SECONDS
import static org.slf4j.LoggerFactory.getLogger

class DocumentMetricsCount implements FiberDefinition {

    private static final LOG = getLogger(DocumentMetricsCount)

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