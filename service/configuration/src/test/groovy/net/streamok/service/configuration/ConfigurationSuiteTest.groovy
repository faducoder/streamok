package net.streamok.service.configuration

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.lib.conf.Conf
import net.streamok.lib.mongo.EmbeddedMongo
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import static net.streamok.service.configuration.ConfigurationRead.configurationRead

@RunWith(VertxUnitRunner)
class ConfigurationSuiteTest {

    static int mongoPort = 1024 + new Random().nextInt(10000)

    @BeforeClass
    static void beforeClass() {
        Conf.configuration().instance().addProperty('MONGO_SERVICE_PORT', mongoPort)
        bus = new DefaultFiberNode().addSuite(new ConfigurationSuite()).vertx().eventBus()
    }

    static def mongo = new EmbeddedMongo().start(mongoPort)

    static EventBus bus

    @Test
    void shouldReadWrittenConfiguration(TestContext context) {
        def async = context.async()
        bus.send('configuration.write', null, new DeliveryOptions().addHeader('key', 'foo').addHeader('value', 'bar')) {
            bus.send(configurationRead, null, new DeliveryOptions().addHeader('key', 'foo')) {
                context.assertEquals(it.result().body(), 'bar')
                async.complete()
            }
        }
    }

    @Test
    void shouldReadNullValue(TestContext context) {
        def async = context.async()
        bus.send(configurationRead, null, new DeliveryOptions().addHeader('key', 'invalidKey')) {
            context.assertEquals(it.result().body(), null)
            async.complete()
        }
    }

    @Test
    void shouldUpdateEntry(TestContext context) {
        def async = context.async()
        bus.send('configuration.write', null, new DeliveryOptions().addHeader('key', 'foo').addHeader('value', 'bar')) {
            bus.send('configuration.write', null, new DeliveryOptions().addHeader('key', 'foo').addHeader('value', 'baz')) {
                bus.send(configurationRead, null, new DeliveryOptions().addHeader('key', 'foo')) {
                    context.assertEquals(it.result().body(), 'baz')
                    async.complete()
                }
            }
        }
    }

}
