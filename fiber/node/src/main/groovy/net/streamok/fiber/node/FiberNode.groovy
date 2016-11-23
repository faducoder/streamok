package net.streamok.fiber.node

import io.vertx.core.Vertx

class FiberNode {

    private final def vertx = Vertx.vertx()

    void addFiber(FiberDefinition fiberDefinition) {
        vertx.eventBus().consumer(fiberDefinition.address(), fiberDefinition.handler())
    }

    void addEndpoint(Endpoint endpoint) {
        endpoint.connect(this)
    }

    Vertx vertx() {
        vertx
    }

}