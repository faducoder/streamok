package net.streamok.service.configuration

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.lib.mongo.MongoClientFactory

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
        new MongoClientFactory().mongoClient(vertx)
    }

}