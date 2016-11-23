package net.streamok.service.configuration

import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition

class ConfigurationGet implements FiberDefinition {

    @Override
    String address() {
        'configuration.get'
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