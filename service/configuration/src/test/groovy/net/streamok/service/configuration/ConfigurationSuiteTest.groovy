package net.streamok.service.configuration

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.FiberNode
import net.streamok.lib.mongo.EmbeddedMongo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class ConfigurationSuiteTest {

    static def mongo = new EmbeddedMongo().start()

    static def bus = new FiberNode().addSuite(new ConfigurationSuite()).vertx().eventBus()

    @Test
    void shouldReadWrittenConfiguration(TestContext context) {
        def async = context.async()
        bus.send('configuration.put', null, new DeliveryOptions().addHeader('key', 'foo').addHeader('value', 'bar')) {
            bus.send('configuration.get', null, new DeliveryOptions().addHeader('key', 'foo')) {
                context.assertEquals(it.result().body(), 'bar')
                async.complete()
            }
        }
    }

    @Test
    void shouldUpdateEntry(TestContext context) {
        def async = context.async()
        bus.send('configuration.put', null, new DeliveryOptions().addHeader('key', 'foo').addHeader('value', 'bar')) {
            bus.send('configuration.put', null, new DeliveryOptions().addHeader('key', 'foo').addHeader('value', 'baz')) {
                bus.send('configuration.get', null, new DeliveryOptions().addHeader('key', 'foo')) {
                    context.assertEquals(it.result().body(), 'baz')
                    async.complete()
                }
            }
        }
    }

}
