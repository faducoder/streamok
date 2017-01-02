package net.streamok.adapter.rest

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.fiber.node.FiberDefinitionFactory
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class RestEndpointTest {

    @Test
    void shouldInvokeOperationViaRestEndpoint(TestContext context) {
        def async = context.async()
        def fiberNode = new DefaultFiberNode()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.body())}']
        fiberNode.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        fiberNode.addEndpoint(new RestEndpoint())

        fiberNode.vertx().createHttpClient().getNow(8080, 'localhost', '/echo') {
            it.bodyHandler {
                fiberNode.vertx().close()
                context.assertEquals(it.toString(), 'null')
                async.complete()
            }
        }
    }

    @Test
    void shouldCopyParameterToHeader(TestContext context) {
        def async = context.async()
        def fiberNode = new DefaultFiberNode()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.nonBlankHeader("foo"))}']
        fiberNode.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        fiberNode.addEndpoint(new RestEndpoint())

        fiberNode.vertx().createHttpClient().getNow(8080, 'localhost', '/echo?foo=bar') {
            it.bodyHandler {
                fiberNode.vertx().close()
                context.assertEquals(it.toString(), 'bar')
                async.complete()
            }
        }
    }

}