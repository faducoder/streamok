package net.streamok.service.document

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import net.streamok.fiber.node.TimerEndpoint
import net.streamok.fiber.node.TimerEvent
import net.streamok.fiber.node.api.*
import net.streamok.service.document.dependencies.MongoClientProvider
import net.streamok.service.document.metrics.DocumentsCountMetric
import net.streamok.service.document.operations.*

import static java.lang.System.currentTimeMillis

class DocumentService implements Service, FiberNodeAware {

    Vertx vertx

    @Override
    List<OperationDefinition> fiberDefinitions() {
        [new DocumentSave(), new DocumentFindOne(), new DocumentFindMany(), new DocumentFind(), new DocumentCount(), new DocumentRemove(), new DocumentsCountMetric(vertx)]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new MongoClientProvider(vertx)]
    }

    @Override
    List<Endpoint> endpoints() {
        [new TimerEndpoint(5000, 'metrics.put', {
            new TimerEvent(deliveryOptions: new DeliveryOptions().
                    addHeader('key', 'service.document.heartbeat').addHeader('value', "${currentTimeMillis()}")) })]
    }

    @Override
    void fiberNode(FiberNode fiberNode) {
        vertx = fiberNode.vertx()
    }
}