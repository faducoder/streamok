package net.streamok.service.configuration

import io.vertx.core.Vertx
import net.streamok.fiber.node.FiberNode
import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberNodeAware
import net.streamok.fiber.node.api.FiberSuite

class ConfigurationSuite implements FiberSuite, FiberNodeAware {

    private Vertx vertx

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new ConfigurationRead(), new ConfigurationWrite()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new ConfigurationStoreProvider(vertx)]
    }

    @Override
    void fiberNode(FiberNode fiberNode) {
        vertx = fiberNode.vertx()
    }

}
