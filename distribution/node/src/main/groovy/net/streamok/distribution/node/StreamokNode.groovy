package net.streamok.distribution.node

import net.streamok.adapter.rest.RestEndpoint
import net.streamok.fiber.node.DefaultServicesNode
import net.streamok.fiber.node.api.ServicesNode
import net.streamok.service.configuration.ConfigurationSuite
import net.streamok.service.document.DocumentService
import net.streamok.service.machinelearning.MachineLearningService
import net.streamok.service.machinelearningrest.MachineLearningRestService
import net.streamok.service.metrics.MetricsSuite
import net.streamok.service.speech.SpeechService

class StreamokNode {

    def fiberNode = new DefaultServicesNode().
            addEndpoint(new RestEndpoint()).
            addSuite(new MetricsSuite()).
            addSuite(new ConfigurationSuite()).
            addSuite(new DocumentService()).
            addSuite(new MachineLearningService()).
            addSuite(new MachineLearningRestService()).
            addSuite(new SpeechService()).
            start()

    ServicesNode fiberNode() {
        fiberNode
    }

    public static void main(String... args) {
        new StreamokNode()
    }

}
