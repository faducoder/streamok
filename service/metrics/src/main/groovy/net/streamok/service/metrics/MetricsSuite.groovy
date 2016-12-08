package net.streamok.service.metrics

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.Service

class MetricsSuite implements Service {

    @Override
    List<OperationDefinition> operations() {
        [new MetricsGet(), new MetricsGetAll(), new MetricsPut()]
    }

    @Override
    List<DependencyProvider> dependencies() {
        [new MetricsStoreProvider()]
    }

    @Override
    List<Endpoint> endpoints() {
        []
    }

}