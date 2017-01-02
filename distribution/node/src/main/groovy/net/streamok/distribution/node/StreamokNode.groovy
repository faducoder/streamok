package net.streamok.distribution.node

import net.streamok.adapter.rest.RestEndpoint
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.fiber.node.api.FiberNode
import net.streamok.service.configuration.ConfigurationSuite
import net.streamok.service.document.DocumentService
import net.streamok.service.machinelearning.MachineLearningService
import net.streamok.service.metrics.MetricsSuite
import net.streamok.service.speech.SpeechService

class StreamokNode {

    def fiberNode = new DefaultFiberNode().
            addEndpoint(new RestEndpoint()).
            addSuite(new MetricsSuite()).
            addSuite(new ConfigurationSuite()).
            addSuite(new DocumentService()).
            addSuite(new MachineLearningService()).
            addSuite(new SpeechService()).
            start()

    FiberNode fiberNode() {
        fiberNode
    }

    public static void main(String... args) {
        new StreamokNode()
    }

}
