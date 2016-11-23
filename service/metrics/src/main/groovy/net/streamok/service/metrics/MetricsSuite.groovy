package net.streamok.service.metrics

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberSuite

class MetricsSuite implements FiberSuite {

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new MetricsGet(), new MetricsGetAll(), new MetricsPut()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new MetricsStoreProvider()]
    }

}