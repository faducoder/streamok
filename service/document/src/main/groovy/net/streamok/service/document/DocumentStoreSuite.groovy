package net.streamok.service.document

import io.vertx.core.Vertx
import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberNode
import net.streamok.fiber.node.api.FiberNodeAware
import net.streamok.fiber.node.api.FiberSuite

class DocumentStoreSuite implements FiberSuite, FiberNodeAware {

    Vertx vertx

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new DocumentStore(), new DocumentFindOne(), new DocumentFindMany(), new DocumentFind()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new MongoDriverProvider(), new MongoClientProvider(vertx)]
    }

    @Override
    List<Endpoint> endpoints() {
        []
    }

    @Override
    void fiberNode(FiberNode fiberNode) {
        vertx = fiberNode.vertx()
    }
}