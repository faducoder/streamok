package net.streamok.service.metrics

import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition

class MetricsPut implements OperationDefinition {

    @Override
    String address() {
        'metrics.put'
    }

    @Override
    OperationHandler handler() {
        { fiberContext ->
            def key = fiberContext.header('key').toString()
            def value = fiberContext.header('value').toString()
            fiberContext.dependency(Map)[key] = value
            fiberContext.reply(null)
        }
    }

}