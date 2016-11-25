package net.streamok.fiber.node

import io.vertx.core.Vertx
import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberNode
import net.streamok.fiber.node.api.FiberNodeAware
import net.streamok.fiber.node.api.FiberSuite

class DefaultFiberNode implements FiberNode {

    private final def vertx = Vertx.vertx()

    def dependencies = [:]

    DefaultFiberNode addFiber(FiberDefinition fiberDefinition) {
        vertx.eventBus().consumer(fiberDefinition.address()){fiberDefinition.handler().handle(new FiberContext(it, this))}
        this
    }

    DefaultFiberNode addDependency(DependencyProvider dependencyProvider) {
        dependencies[dependencyProvider.key()] = dependencyProvider.dependency()
        this
    }

    DefaultFiberNode addEndpoint(Endpoint endpoint) {
        endpoint.connect(this)
        this
    }

    DefaultFiberNode addSuite(FiberSuite fiberSuite) {
        if(fiberSuite instanceof FiberNodeAware) {
            fiberSuite.fiberNode(this)
        }
        fiberSuite.dependencyProviders().each { addDependency(it) }
        fiberSuite.fiberDefinitions().each { addFiber(it) }
        this
    }

    Object dependency(String key) {
        dependencies[key]
    }

    def <T> T dependency(Class<T> type) {
        dependencies.values().find { type.isAssignableFrom(it.getClass()) }
    }

    Vertx vertx() {
        vertx
    }

}