package net.streamok.distribution.node

import net.streamok.fiber.node.FiberNode
import net.streamok.fiber.node.RestEndpoint
import net.streamok.service.configuration.ConfigurationSuite

class StreamokNode {

    public static void main(String... args) {
        new FiberNode().
                addEndpoint(new RestEndpoint()).
                addSuite(new ConfigurationSuite())
    }

}
