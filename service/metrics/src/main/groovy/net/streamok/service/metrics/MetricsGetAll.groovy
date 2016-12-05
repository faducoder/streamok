package net.streamok.service.metrics

import io.vertx.core.json.Json
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition

class MetricsGetAll implements OperationDefinition {

    @Override
    String address() {
        'metrics.getAll'
    }

    @Override
    OperationHandler handler() {
        { fiberContext ->
            def value = fiberContext.dependency(Map)
            fiberContext.reply(Json.encode(value))
        }
    }

}