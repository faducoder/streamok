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

            def mongoIds = new BasicDBObject('$in', documentIds.toList())
            def query = new BasicDBObject('_id', mongoIds)
            mongo.find(collection, new JsonObject(query.toMap())) {
                def results = it.result().collect { new MongodbMapper().mongoToCanonical(new BasicDBObject(mongoIds.toMap())) }
                fiberContext.reply(Json.encode(results))
            }
        }
    }

}