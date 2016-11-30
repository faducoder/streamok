package net.streamok.service.document.operations

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.service.document.MongodbMapper

import static org.apache.commons.lang3.Validate.notBlank
import static org.apache.commons.lang3.Validate.notNull
import static org.slf4j.LoggerFactory.getLogger

class DocumentFindOne implements FiberDefinition {

    private static final LOG = getLogger(DocumentFindOne)

    @Override
    String address() {
        'document.findOne'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def collection = notNull(fiberContext.header('collection'), "Collection can't  be null.").toString()
            collection = notBlank(collection, "Collection can't be blank.")

            def id = notNull(fiberContext.header('id'), 'Document ID not expected to be null.').toString()
            id = notBlank(id, 'Document ID not expected to be blank.')

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