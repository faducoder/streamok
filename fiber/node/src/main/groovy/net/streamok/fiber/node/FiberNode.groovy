package net.streamok.fiber.node

import io.vertx.core.Vertx
import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberNodeAware
import net.streamok.fiber.node.api.FiberSuite

class FiberNode {

    private final def vertx = Vertx.vertx()

    def dependencies = [:]

    FiberNode addFiber(FiberDefinition fiberDefinition) {
        vertx.eventBus().consumer(fiberDefinition.address()){fiberDefinition.handler().handle(new FiberContext(it, this))}
        this
    }

    FiberNode addDependency(DependencyProvider dependencyProvider) {
        dependencies[dependencyProvider.key()] = dependencyProvider.dependency()
        this
    }

    FiberNode addEndpoint(Endpoint endpoint) {
        endpoint.connect(this)
        this
    }

    FiberNode addSuite(FiberSuite fiberSuite) {
        if(fiberSuite instanceof FiberNodeAware) {
            fiberSuite.fiberNode(this)
        }
        fiberSuite.dependencyProviders().each { addDependency(it) }
        fiberSuite.fiberDefinitions().each { addFiber(it) }
        this
    }

    Vertx vertx() {
        vertx
    }

}