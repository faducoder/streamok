package net.streamok.service.configuration

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import net.streamok.fiber.node.TimerEndpoint
import net.streamok.fiber.node.TimerEvent
import net.streamok.fiber.node.api.*

import static java.lang.System.currentTimeMillis

class ConfigurationSuite implements Service, FiberNodeAware {

    private Vertx vertx

    @Override
    List<OperationDefinition> operations() {
        [new ConfigurationRead(), new ConfigurationWrite()]
    }

    @Override
    List<DependencyProvider> dependencies() {
        [new ConfigurationStoreProvider(vertx)]
    }

    @Override
    List<Endpoint> endpoints() {
        [new TimerEndpoint(5000, 'metrics.put', {
            new TimerEvent(deliveryOptions: new DeliveryOptions().
                    addHeader('key', 'service.configuration.heartbeat').addHeader('value', "${currentTimeMillis()}")) })]
    }

    @Override
    void fiberNode(ServicesNode fiberNode) {
        vertx = fiberNode.vertx()
    }

}
