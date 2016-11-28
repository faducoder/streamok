package net.streamok.fiber.node

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class DefaultFiberNodeTest {

    @Test
    void shouldInvokeGroovyClosureFiber(TestContext context) {
        def async = context.async()
        def fiberNode = new DefaultFiberNode().start()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.body())}']
        fiberNode.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        fiberNode.vertx().eventBus().send('echo', 'foo') {
            context.assertEquals(it.result().body(), 'foo')
            async.complete()
        }
    }

}
