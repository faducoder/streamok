package net.streamok.service.configuration

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.DependencyProvider

class ConfigurationStoreProvider implements DependencyProvider {

    Vertx vertx

    ConfigurationStoreProvider(Vertx vertx) {
        this.vertx = vertx
    }

    @Override
    String key() {
        'configuration.store'
    }

    @Override
    Object dependency() {
        MongoClient.createShared(vertx, new JsonObject([host: 'localhost', port: 27017]))
    }

}