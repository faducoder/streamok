package net.streamok.fiber.node

import io.vertx.core.Vertx
import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition

class FiberNode {

    private final def vertx = Vertx.vertx()

    def dependencies = [:]

    void addFiber(FiberDefinition fiberDefinition) {
        vertx.eventBus().consumer(fiberDefinition.address()){fiberDefinition.handler().handle(new FiberContext(it, this))}
    }

    void addDependency(DependencyProvider dependencyProvider) {
        dependencies[dependencyProvider.key()] = dependencyProvider.dependency()
    }

    void addEndpoint(Endpoint endpoint) {
        endpoint.connect(this)
    }

    Vertx vertx() {
        vertx
    }

}