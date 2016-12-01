package net.streamok.distribution.chaosmonkey

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.distribution.node.StreamokNode
import net.streamok.lib.mongo.EmbeddedMongo
import org.junit.Test
import org.junit.runner.RunWith

import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class ChaosMonkeyTest {

    @Test
    void shouldSurviveMonkeyRun(TestContext testContext) {
        def async = testContext.async()
        new EmbeddedMongo().start()
        def node = new StreamokNode()
        new ChaosMonkey().run()
        node.fiberNode().vertx().eventBus().send('metrics.get', null, new DeliveryOptions().addHeader('key', "fiber.node.${node.fiberNode.id()}.started")) {
            def started = it.result().body().toString().toLong()
            assertThat(started).isGreaterThan(0L)
            async.complete()
        }
    }

}
