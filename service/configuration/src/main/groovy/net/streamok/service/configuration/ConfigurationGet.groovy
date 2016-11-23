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
        { FiberContext fiberContext ->
            def key = fiberContext.header('key').toString()
            def store = fiberContext.dependency('configuration.store') as Map
            fiberContext.reply(store[key])
        }
    }

}