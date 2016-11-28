package net.streamok.lib.mongo

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient

import static com.google.common.base.MoreObjects.firstNonNull
import static java.lang.System.getenv

class MongoClientFactory {

    MongoClient mongoClient(Vertx vertx, String host, int port) {
        MongoClient.createShared(vertx, new JsonObject([host: host, port: port]))
    }

    MongoClient mongoClient(Vertx vertx) {
        def host = firstNonNull(getenv('MONGO_SERVICE_HOST'), 'localhost')
        def port = firstNonNull(getenv('MONGO_SERVICE_PORT'), '27017')
        mongoClient(vertx, host, port.toInteger())
    }

}
