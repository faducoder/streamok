package net.streamok.lib.mongo

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.lib.conf.Conf

import static com.google.common.base.MoreObjects.firstNonNull
import static java.lang.System.getenv
import static net.streamok.lib.conf.Conf.configuration

class MongoClientFactory {

    MongoClient mongoClient(Vertx vertx, String host, int port) {
        MongoClient.createShared(vertx, new JsonObject([host: host, port: port]))
    }

    MongoClient mongoClient(Vertx vertx) {
        def host = configuration().get().getString('MONGO_SERVICE_HOST', 'localhost')
        def port = configuration().get().getInt('MONGO_SERVICE_PORT', 27017)
        mongoClient(vertx, host, port)
    }

}
