package net.streamok.service.configuration

import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition

class ConfigurationPut implements FiberDefinition {

    @Override
    String address() {
        'configuration.put'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def key = fiberContext.header('key').toString()
            def value = fiberContext.header('value').toString()
            def store = fiberContext.dependency('configuration.store') as Map
            store[key] = value
            fiberContext.reply(null)
        }
    }

}