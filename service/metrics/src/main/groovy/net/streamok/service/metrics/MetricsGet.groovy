package net.streamok.service.metrics

import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition

class MetricsGet implements OperationDefinition {

    @Override
    String address() {
        'metrics.get'
    }

    @Override
    OperationHandler handler() {
        { fiberContext ->
            def key = fiberContext.header('key').toString()
            def value = fiberContext.dependency(Map)[key]
            fiberContext.reply(value)
        }
    }

}