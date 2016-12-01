package net.streamok.fiber.node

import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberNode

class TimerEndpoint implements Endpoint {

    protected final long delay

    protected final String address

    protected Closure<TimerEvent> event

    TimerEndpoint(long delay, String address, Closure<TimerEvent> event) {
        this.delay = delay
        this.address = address
        this.event = event
    }

    TimerEndpoint(long delay, String address) {
        this.delay = delay
        this.address = address
    }

    @Override
    void connect(FiberNode fiberNode) {
        fiberNode.vertx().setPeriodic(delay) {
            def ev = event()
            fiberNode.vertx().eventBus().send(this.address, ev.body, ev.deliveryOptions)
        }
    }

}
