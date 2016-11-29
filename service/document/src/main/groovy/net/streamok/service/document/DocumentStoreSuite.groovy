package net.streamok.service.document

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberSuite

class DocumentStoreSuite implements FiberSuite {

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new DocumentStore(), new DocumentFindOne(), new DocumentFind()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new MongoDriverProvider()]
    }

    @Override
    List<Endpoint> endpoints() {
        []
    }

}