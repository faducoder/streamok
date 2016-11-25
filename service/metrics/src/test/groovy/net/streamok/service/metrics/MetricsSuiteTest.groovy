package net.streamok.service.metrics

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class MetricsSuiteTest {

    def bus = new DefaultFiberNode().addSuite(new MetricsSuite()).vertx().eventBus()

    @Test
    void shouldReadWrittenConfiguration(TestContext context) {
        def async = context.async()
        bus.send('metrics.put', null, new DeliveryOptions().addHeader('key', 'foo').addHeader('value', 'bar')) {
            bus.send('metrics.get', null, new DeliveryOptions().addHeader('key', 'foo')) {
                context.assertEquals(it.result().body(), 'bar')
                async.complete()
            }
        }
    }

    @Test
    void shouldGetAll(TestContext context) {
        def async = context.async()
        bus.send('metrics.put', null, new DeliveryOptions().addHeader('key', 'foo').addHeader('value', 'bar')) {
            bus.send('metrics.getAll', null) {
                def x = Json.decodeValue(it.result().body().toString(), Map)
                context.assertTrue(x.containsKey('foo'))
                async.complete()
            }
        }
    }

}