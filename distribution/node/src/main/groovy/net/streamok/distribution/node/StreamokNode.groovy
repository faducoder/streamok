package net.streamok.distribution.node

import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.fiber.node.RestEndpoint
import net.streamok.service.configuration.ConfigurationSuite
import net.streamok.service.metrics.MetricsSuite

class StreamokNode {

    public static void main(String... args) {
        new DefaultFiberNode().
                addEndpoint(new RestEndpoint()).
                addSuite(new MetricsSuite()).
                addSuite(new ConfigurationSuite())
    }

}
