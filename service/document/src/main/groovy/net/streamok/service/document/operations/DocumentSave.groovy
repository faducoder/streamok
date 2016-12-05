package net.streamok.service.document.operations

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.service.document.MongodbMapper

import static io.vertx.core.json.Json.encode
import static org.slf4j.LoggerFactory.getLogger

class DocumentSave implements OperationDefinition {

    private static final LOG = getLogger(DocumentSave)

    public static final String documentSave = 'document.save'

    @Override
    String address() {
        documentSave
    }

    @Override
    OperationHandler handler() {
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