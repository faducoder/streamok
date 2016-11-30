package net.streamok.service.document.operations

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition

import static org.apache.commons.lang3.Validate.notBlank
import static org.apache.commons.lang3.Validate.notNull
import static org.slf4j.LoggerFactory.getLogger

class DocumentRemove implements FiberDefinition {

    private static final LOG = getLogger(DocumentRemove)

    @Override
    String address() {
        'document.remove'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def collection = fiberContext.header('collection').toString()
            notNull(collection, 'Document collection expected not to be null.')

            def id = notNull(fiberContext.header('id'), 'Document ID not expected to be null.').toString()
            id = notBlank(id, 'Document ID not expected to be blank.')

            def mongo = fiberContext.dependency(MongoClient)


            LOG.debug('Removing document with ID {} from collection {}.', id, collection)

            mongo.remove(collection, new JsonObject([_id: id])) {
                fiberContext.reply(null)
            }
        }
    }

}