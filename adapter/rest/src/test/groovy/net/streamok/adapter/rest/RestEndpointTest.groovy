package net.streamok.adapter.rest

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.fiber.node.FiberDefinitionFactory
import net.streamok.fiber.node.api.FiberNode
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static net.streamok.lib.common.Networks.findAvailableTcpPort
import static net.streamok.lib.conf.Conf.configuration

@RunWith(VertxUnitRunner)
class RestEndpointTest {

    int port = findAvailableTcpPort()

    FiberNode fiberNode

    @Before
    void before() {
        configuration().memory().setProperty('adapter.rest.port', port)
        fiberNode = new DefaultFiberNode().addEndpoint(new RestEndpoint())
    }

    @After
    void after() {
        fiberNode.close()
    }

    @Test
    void shouldInvokeOperationViaRestEndpoint(TestContext context) {
        def async = context.async()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.body())}']
        fiberNode.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        fiberNode.vertx().createHttpClient().getNow(port, 'localhost', '/echo') {
            it.bodyHandler {
                context.assertEquals(it.toString(), 'null')
                async.complete()
            }
        }
    }

    @Test
    void shouldCopyParameterToHeader(TestContext context) {
        def async = context.async()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.nonBlankHeader("foo"))}']
        fiberNode.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        fiberNode.vertx().createHttpClient().getNow(port, 'localhost', '/echo?foo=bar') {
            it.bodyHandler {
                context.assertEquals(it.toString(), 'bar')
                async.complete()
            }
        }
    }

}