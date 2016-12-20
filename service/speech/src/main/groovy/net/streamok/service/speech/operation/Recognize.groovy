package net.streamok.service.speech.operation

import com.google.cloud.speech.spi.v1beta1.SpeechClient
import com.google.cloud.speech.v1beta1.RecognitionAudio
import com.google.cloud.speech.v1beta1.RecognitionConfig
import com.google.protobuf.ByteString
import io.vertx.core.json.Json
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler

import java.util.concurrent.Executors

import static com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding.LINEAR16

class Recognize implements OperationDefinition {

    def executor = Executors.newCachedThreadPool()

    @Override
    String address() {
        'speech.recognize'
    }

    @Override
    OperationHandler handler() {
        { operation ->
            def client = SpeechClient.create()
            def config = RecognitionConfig.newBuilder().setSampleRate(16000).setEncoding(LINEAR16).build()

            def audioBytes = operation.body(byte[])
            def audio = RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(audioBytes)).build()

            def result = client.asyncRecognizeAsync(config, audio)
            result.addListener({
                def transcriptResult = result.get().resultsList.first().alternativesList.first().transcript
                operation.reply(Json.encode([transcript: transcriptResult]))
            }, executor)
        }
    }

}
