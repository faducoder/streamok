package net.streamok.service.document

import com.mongodb.BasicDBObject
import com.mongodb.Mongo
import io.vertx.core.json.Json
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
            def mongo = fiberContext.dependency(Mongo)

            Validate.notNull(collection, 'Document collection expected not to be null.')

            def col = mongo.getDB('default_db').getCollection(collection)

            def mongoIds = new BasicDBObject('$in', documentIds.collect{new ObjectId(it)})
            def query = new BasicDBObject('_id', mongoIds)
            def results = col.find(query).toArray().collect { new MongodbMapper().mongoToCanonical(it) }
            fiberContext.reply(Json.encode(results))
        }
    }

}