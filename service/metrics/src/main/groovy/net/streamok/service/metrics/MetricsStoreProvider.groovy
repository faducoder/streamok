package net.streamok.service.metrics

import net.streamok.fiber.node.api.DependencyProvider

class MetricsStoreProvider implements DependencyProvider {

    @Override
    String key() {
        'metrics.store'
    }

    @Override
    Object dependency() {
        [:]
    }

}