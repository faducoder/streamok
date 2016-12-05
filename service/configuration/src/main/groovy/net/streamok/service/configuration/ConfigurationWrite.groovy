package net.streamok.service.configuration

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition

class ConfigurationWrite implements OperationDefinition {

    @Override
    String address() {
        'configuration.write'
    }

    @Override
    OperationHandler handler() {
        { fiberContext ->
            def key = fiberContext.header('key').toString()
            def value = fiberContext.header('value').toString()
            def mongo = fiberContext.dependency(MongoClient)
            mongo.findOne('configuration', new JsonObject().put('key', key), null) {
                if(it.result()) {
                    it.result().put('value', value)
                    mongo.save('configuration', it.result()) {
                        fiberContext.reply(null)
                    }
                } else {
                    mongo.save('configuration', new JsonObject([key: key, value: value])) {
                        fiberContext.reply(null)
                    }
                }
            }
        }
    }

}