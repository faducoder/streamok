package net.streamok.fiber.node

import io.vertx.core.eventbus.DeliveryOptions
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberNode

class TimerEndpoint implements Endpoint {

    private final long delay

    private final String address

    private final Closure<Event> event

    TimerEndpoint(long delay, String address, Closure<Event> event) {
        this.delay = delay
        this.address = address
        this.event = event
    }

    @Override
    void connect(FiberNode fiberNode) {
        fiberNode.vertx().setPeriodic(delay) {
            def ev = event()
            fiberNode.vertx().eventBus().send(address, ev.body, ev.deliveryOptions)
        }
    }

    static class Event {

        DeliveryOptions deliveryOptions

        Object body

    }

}
