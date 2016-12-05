package net.streamok.service.configuration

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition

class ConfigurationRead implements OperationDefinition {

    public static final String configurationRead = 'configuration.read'

    @Override
    String address() {
        configurationRead
    }

    @Override
    OperationHandler handler() {
        { fiberContext ->
            def key = fiberContext.header('key').toString()
            def mongo = fiberContext.dependency(MongoClient)
            mongo.findOne('configuration', new JsonObject().put('key', key), null) {
                def result =  it.result()
                if(result) {
                    fiberContext.reply(result.getString('value'))
                } else {
                    fiberContext.reply(null)
                }
            }
        }
    }

}