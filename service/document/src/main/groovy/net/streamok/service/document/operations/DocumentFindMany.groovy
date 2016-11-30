package net.streamok.service.document.operations

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.service.document.MongodbMapper
import org.apache.commons.lang3.Validate

import static org.slf4j.LoggerFactory.getLogger

class DocumentFindMany implements FiberDefinition {

    private static final LOG = getLogger(DocumentFindMany)

    @Override
    String address() {
        'document.findMany'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def collection = fiberContext.header('collection').toString()
            def documentIds = fiberContext.body(String[])
            def mongo = fiberContext.dependency(MongoClient)

            Validate.notNull(collection, 'Document collection expected not to be null.')

            def mongoIds = ['$in': documentIds.toList()]
            def query = ['_id': mongoIds]
            mongo.find(collection, new JsonObject(query)) {
                def results = it.result().collect { new MongodbMapper().mongoToCanonical(mongoIds) }
                fiberContext.reply(Json.encode(results))
            }
        }
    }

}