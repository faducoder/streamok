package net.streamok.service.metrics

import io.vertx.core.json.Json
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition

class MetricsGetAll implements FiberDefinition {

    @Override
    String address() {
        'metrics.getAll'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def value = fiberContext.dependency(Map)
            fiberContext.reply(Json.encode(value))
        }
    }

}