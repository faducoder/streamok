package net.streamok.lib.vertx

import io.vertx.core.Vertx
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.Test
import org.junit.runner.RunWith

import static net.streamok.lib.vertx.Handlers.completeIteration

@RunWith(VertxUnitRunner)
class HandlersTest {

    @Test
    void shouldExecuteFinishCallbackOnce(TestContext context) {
        def async = context.async()
        def bus = Vertx.vertx().eventBus()
        bus.consumer('echo') {
            it.reply(it.body())
        }

        completeIteration([1, 2, 3]) { iteration ->
            bus.send('echo', iteration.element()) {
                iteration.ifFinished {
                    async.complete()
                }
            }
        }
    }

}
