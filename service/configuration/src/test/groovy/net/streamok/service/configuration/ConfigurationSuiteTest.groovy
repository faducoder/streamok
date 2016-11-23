package net.streamok.service.configuration

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.FiberNode
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class ConfigurationSuiteTest {

    def bus = new FiberNode().addSuite(new ConfigurationSuite()).vertx().eventBus()

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

}
