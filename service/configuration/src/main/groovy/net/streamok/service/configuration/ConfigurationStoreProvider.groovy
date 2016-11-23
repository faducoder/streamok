package net.streamok.service.configuration

import net.streamok.fiber.node.api.DependencyProvider

class ConfigurationStoreProvider implements DependencyProvider {

    @Override
    String key() {
        'configuration.store'
    }

    @Override
    Object dependency() {
        [:]
    }

}