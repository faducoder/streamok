package net.streamok.service.document.operations

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.service.document.MongodbMapper

import static io.vertx.core.json.Json.encode
import static org.slf4j.LoggerFactory.getLogger

class DocumentSave implements FiberDefinition {

    private static final LOG = getLogger(DocumentSave)

    @Override
    String address() {
        'document.save'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def pojo = fiberContext.body()
            def collection = fiberContext.header('collection').toString()
            def mongo = fiberContext.dependency(MongoClient)

            LOG.debug('About to save {} into {}.', pojo, collection)

            def document = new MongodbMapper().canonicalToMongo(pojo)
            mongo.save(collection, new JsonObject(document)) {
                fiberContext.reply(encode(it.result()))
            }
        }
    }

}