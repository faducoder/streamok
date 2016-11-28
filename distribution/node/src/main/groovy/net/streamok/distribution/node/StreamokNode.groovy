package net.streamok.distribution.node

import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.fiber.node.RestEndpoint
import net.streamok.fiber.node.api.FiberNode
import net.streamok.service.configuration.ConfigurationSuite
import net.streamok.service.metrics.MetricsSuite

class StreamokNode {

    def fiberNode = new DefaultFiberNode().
            addEndpoint(new RestEndpoint()).
            addSuite(new MetricsSuite()).
            addSuite(new ConfigurationSuite()).start()

    FiberNode fiberNode() {
        fiberNode
    }

    public static void main(String... args) {
        new StreamokNode()
    }

}
