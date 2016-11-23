package net.streamok.distribution.node

import net.streamok.fiber.node.FiberDefinitionFactory
import net.streamok.fiber.node.FiberNode
import net.streamok.fiber.node.RestEndpoint
import net.streamok.service.configuration.ConfigurationGet
import net.streamok.service.configuration.ConfigurationPut
import net.streamok.service.configuration.ConfigurationStoreProvider

class StreamokNode {

    public static void main(String... args) {
        def node = new FiberNode()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.body())}']
        node.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        node.addEndpoint(new RestEndpoint())

        node.addDependency(new ConfigurationStoreProvider())
        node.addFiber(new ConfigurationGet())
        node.addFiber(new ConfigurationPut())
    }

}
