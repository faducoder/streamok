package net.streamok.fiber.node

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class RestEndpointTest {

    @Test
    void shouldInvokeFiberViaRestEndpoint(TestContext context) {
        def async = context.async()
        def fiberNode = new FiberNode()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.body())}']
        fiberNode.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        fiberNode.addEndpoint(new RestEndpoint())

        fiberNode.vertx.createHttpClient().getNow(8080, 'localhost', '/echo') {
            it.bodyHandler {
                context.assertEquals(it.toString(), 'null')
                async.complete()
            }
        }
    }

}
