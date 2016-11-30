package net.streamok.service.document

import io.vertx.core.Vertx
import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberNode
import net.streamok.fiber.node.api.FiberNodeAware
import net.streamok.fiber.node.api.Service
import net.streamok.service.document.dependencies.MongoClientProvider
import net.streamok.service.document.operations.DocumentCount
import net.streamok.service.document.operations.DocumentFind
import net.streamok.service.document.operations.DocumentFindMany
import net.streamok.service.document.operations.DocumentFindOne
import net.streamok.service.document.operations.DocumentStore

class DocumentService implements Service, FiberNodeAware {

    Vertx vertx

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new DocumentStore(), new DocumentFindOne(), new DocumentFindMany(), new DocumentFind(), new DocumentCount()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new MongoClientProvider(vertx)]
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