package net.streamok.service.configuration

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import net.streamok.fiber.node.TimerEndpoint
import net.streamok.fiber.node.api.*

import static java.lang.System.currentTimeMillis

class ConfigurationSuite implements FiberSuite, FiberNodeAware {

    private Vertx vertx

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new ConfigurationRead(), new ConfigurationWrite()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new ConfigurationStoreProvider(vertx)]
    }

    @Override
    List<Endpoint> endpoints() {
        [new TimerEndpoint(5000, 'metrics.put', {
            new TimerEndpoint.Event(deliveryOptions: new DeliveryOptions().
                    addHeader('key', 'service.configuration.heartbeat').addHeader('value', "${currentTimeMillis()}")) })]
    }

    @Override
    void fiberNode(FiberNode fiberNode) {
        vertx = fiberNode.vertx()
    }

}
