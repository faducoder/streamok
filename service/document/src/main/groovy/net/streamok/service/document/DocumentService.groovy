package net.streamok.service.document

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import net.streamok.fiber.node.TimerEndpoint
import net.streamok.fiber.node.api.*
import net.streamok.service.document.dependencies.MongoClientProvider
import net.streamok.service.document.metrics.DocumentMetricsCount
import net.streamok.service.document.operations.*

import static java.lang.System.currentTimeMillis

class DocumentService implements Service, FiberNodeAware {

    Vertx vertx

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new DocumentSave(), new DocumentFindOne(), new DocumentFindMany(), new DocumentFind(), new DocumentCount(), new DocumentRemove(), new DocumentMetricsCount()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new MongoClientProvider(vertx)]
    }

    @Override
    List<Endpoint> endpoints() {
        [new TimerEndpoint(5000, 'metrics.put', {
            new TimerEndpoint.Event(deliveryOptions: new DeliveryOptions().
                    addHeader('key', 'service.document.heartbeat').addHeader('value', "${currentTimeMillis()}")) }),
         new TimerEndpoint(15000, 'document.metrics.count', {
             new TimerEndpoint.Event(deliveryOptions: new DeliveryOptions()) })]
    }

    @Override
    void fiberNode(FiberNode fiberNode) {
        vertx = fiberNode.vertx()
    }
}