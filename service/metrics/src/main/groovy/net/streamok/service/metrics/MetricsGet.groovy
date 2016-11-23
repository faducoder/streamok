package net.streamok.service.metrics

import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition

class MetricsGet implements FiberDefinition {

    @Override
    String address() {
        'metrics.get'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def key = fiberContext.header('key').toString()
            def value = fiberContext.dependency(Map)[key]
            fiberContext.reply(value)
        }
    }

}