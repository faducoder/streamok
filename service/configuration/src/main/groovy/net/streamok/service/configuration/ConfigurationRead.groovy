package net.streamok.service.configuration

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition

class ConfigurationRead implements FiberDefinition {

    public static final String configurationRead = 'configuration.read'

    @Override
    String address() {
        configurationRead
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def key = fiberContext.header('key').toString()
            def mongo = fiberContext.dependency(MongoClient)
            mongo.findOne('configuration', new JsonObject().put('key', key), null) {
                fiberContext.reply(it.result().getString('value'))
            }
        }
    }

}