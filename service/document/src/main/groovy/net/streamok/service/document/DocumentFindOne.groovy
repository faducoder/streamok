package net.streamok.service.document

import com.mongodb.Mongo
import io.vertx.core.json.Json
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
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
        new Fiber() {
            @Override
            void handle(FiberContext fiberContext) {
                def collection = fiberContext.header('collection').toString()
                def documentId = fiberContext.header('id').toString()
                def mongo = fiberContext.dependency(Mongo)

                Validate.notNull(documentId, 'Document ID expected not to be null.')
                Validate.notNull(collection, 'Document collection expected not to be null.')

                LOG.debug('Looking up for document with ID {} from collection {}.', documentId, collection)
                def col = mongo.getDB('documents').getCollection(collection)
                def xxx = col.findOne(new ObjectId(documentId))
                if(xxx != null) {
                    fiberContext.reply(Json.encode(new MongodbMapper().mongoToCanonical(xxx)))
                } else {
                    fiberContext.reply(null)
                }
            }
        }
    }

}