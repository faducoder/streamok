package net.streamok.service.speech

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith

import static io.vertx.core.json.Json.decodeValue
import static org.apache.commons.io.IOUtils.toByteArray
import static org.junit.Assume.assumeTrue

@RunWith(VertxUnitRunner)
class SpeechServiceTest {

    def bus = new DefaultFiberNode().addSuite(new SpeechService()).vertx().eventBus()

    @Test
    void shouldRecognizeSpeech(TestContext context) {
        assumeTrue(System.getenv('GOOGLE_APPLICATION_CREDENTIALS') != null)

        def async = context.async()
        bus.send('speech.recognize', toByteArray(getClass().getResourceAsStream('/audio.raw'))) {
            def response = decodeValue(it.result().body() as String, Map)
            Assertions.assertThat(response.transcript as String).isEqualToIgnoringCase('how old is the Brooklyn Bridge')
            async.complete()
        }
    }

}