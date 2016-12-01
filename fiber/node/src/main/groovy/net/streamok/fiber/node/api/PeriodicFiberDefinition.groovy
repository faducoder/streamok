package net.streamok.fiber.node.api

import io.vertx.core.Vertx

abstract class PeriodicFiberDefinition implements FiberDefinition {

    private final Vertx vertx

    private final String address

    private final long delay

    PeriodicFiberDefinition(Vertx vertx, String address, long delay) {
        this.vertx = vertx
        this.address = address
        this.delay = delay

        vertx.setPeriodic(delay) {
            vertx.eventBus().send(address, null)
        }
    }

    @Override
    String address() {
        address
    }

}