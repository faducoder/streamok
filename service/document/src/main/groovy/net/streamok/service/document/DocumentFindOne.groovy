package net.streamok.service.document

import com.mongodb.BasicDBObject
import com.mongodb.Mongo
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import org.apache.commons.lang3.Validate
import org.bson.types.ObjectId

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
            def collection = fiberContext.header('collection').toString()
            def documentId = fiberContext.header('id').toString()
            def mongo = fiberContext.dependency(MongoClient)

            Validate.notNull(documentId, 'Document ID expected not to be null.')
            Validate.notNull(collection, 'Document collection expected not to be null.')

            LOG.debug('Looking up for document with ID {} from collection {}.', documentId, collection)

            mongo.findOne(collection, new JsonObject([_id: documentId]), null) {
                if (it.result() != null) {
                    fiberContext.reply(Json.encode(new MongodbMapper().mongoToCanonical(new BasicDBObject(it.result().map))))
                } else {
                    fiberContext.reply(null)
                }
            }
        }
    }

}