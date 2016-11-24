package net.streamok.service.configuration

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.FiberNode
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class ConfigurationSuiteTest {

    static IMongodConfig mongodConfig = new MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(new Net(27017, Network.localhostIsIPv6()))
            .build();

    static def xx = MongodStarter.getDefaultInstance().prepare(mongodConfig).start()

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
