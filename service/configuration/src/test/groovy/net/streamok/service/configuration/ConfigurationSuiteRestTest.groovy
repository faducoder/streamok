package net.streamok.service.configuration

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.FiberNode
import net.streamok.fiber.node.RestEndpoint
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class ConfigurationSuiteRestTest {

    def httpClient = new FiberNode().addSuite(new ConfigurationSuite()).addEndpoint(new RestEndpoint()).vertx().createHttpClient()

    @Test
    void shouldReadWrittenConfiguration(TestContext context) {
        def async = context.async()
        httpClient.getNow(8080, 'localhost', '/configuration/put?key=foo&value=bar') {
            httpClient.getNow(8080, 'localhost', '/configuration/get?key=foo') {
                it.bodyHandler {
                    context.assertEquals(it.toString(), 'bar')
                    async.complete()
                }
            }
        }
    }

}