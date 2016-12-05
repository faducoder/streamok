package net.streamok.service.document.operations

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.service.document.MongodbMapper

import static org.slf4j.LoggerFactory.getLogger

class DocumentFindOne implements OperationDefinition {

    private static final LOG = getLogger(DocumentFindOne)

    @Override
    String address() {
        'document.findOne'
    }

    @Override
    OperationHandler handler() {
        { fiberContext ->
            def collection = fiberContext.nonBlankHeader('collection')
            def id = fiberContext.nonBlankHeader('id')
            def mongo = fiberContext.dependency(MongoClient)

            LOG.debug('Looking up for document with ID {} from collection {}.', id, collection)

            mongo.findOne(collection, new JsonObject([_id: id]), null) {
                if(it.succeeded()) {
                    if (it.result() != null) {
                        fiberContext.reply(Json.encode(new MongodbMapper().mongoToCanonical(it.result().map)))
                    } else {
                        fiberContext.reply(null)
                    }
                } else {
                    fiberContext.fail(100, "Can't load document with ID ${id} from collection ${collection}. Cause: ${it.cause().getMessage()}")
                }
            }
        }
    }

}