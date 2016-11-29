package net.streamok.service.document

import com.mongodb.Mongo
import io.vertx.core.json.Json
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition
import org.apache.commons.lang3.Validate

import static org.slf4j.LoggerFactory.getLogger

class DocumentFind implements FiberDefinition {

    private static final LOG = getLogger(DocumentFind)

    @Override
    String address() {
        'document.find'
    }

    @Override
    Fiber handler() {
        new Fiber() {
            @Override
            void handle(FiberContext fiberContext) {
                def collection = fiberContext.header('collection').toString()
                def queryBuilder = fiberContext.body(QueryBuilder)
                def mongo = fiberContext.dependency(Mongo)

                Validate.notNull(collection, 'Document collection expected not to be null.')

                def col = mongo.getDB('default_db').getCollection(collection)

                def results = col.find(new MongodbMapper().mongoQuery(queryBuilder.query)).
                            limit(queryBuilder.size).skip(queryBuilder.skip()).sort(new MongodbMapper().sortConditions(queryBuilder)).
                            toArray().collect{ new MongodbMapper().mongoToCanonical(it) }
                fiberContext.reply(Json.encode(results))
            }
        }
    }

}