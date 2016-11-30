package net.streamok.service.document.dependencies

import io.vertx.core.Vertx
import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.lib.mongo.MongoClientFactory

class MongoClientProvider implements DependencyProvider {

    Vertx vertx

    MongoClientProvider(Vertx vertx) {
        this.vertx = vertx
    }

    @Override
    String key() {
        'mongoClient'
    }

    @Override
    Object dependency() {
        new MongoClientFactory().mongoClient(vertx)
    }

}