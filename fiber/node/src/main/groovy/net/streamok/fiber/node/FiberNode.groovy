package net.streamok.fiber.node

import io.vertx.core.Vertx

class FiberNode {

    def vertx = Vertx.vertx()

    void addFiber(FiberDefinition fiberDefinition) {
        vertx.eventBus().consumer(fiberDefinition.address(), fiberDefinition.handler())
    }

}