package net.streamok.service.configuration

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberSuite

class ConfigurationSuite implements FiberSuite {

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new ConfigurationGet(), new ConfigurationPut()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new ConfigurationStoreProvider()]
    }

}
