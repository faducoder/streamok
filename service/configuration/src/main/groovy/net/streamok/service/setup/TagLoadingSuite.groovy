package net.streamok.service.setup

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.Service

class TagLoadingSuite implements Service {

    @Override
    List<OperationDefinition> operations() {
        return [new TagLoadingOperation(), new TagSavingOperation()]
    }

    @Override
    List<DependencyProvider> dependencies() {
        return []
    }

    @Override
    List<Endpoint> endpoints() {
        return []
    }
}
