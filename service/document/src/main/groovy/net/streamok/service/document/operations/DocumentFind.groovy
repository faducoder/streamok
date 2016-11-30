package net.streamok.service.document.operations

import com.mongodb.BasicDBObject
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.service.document.MongodbMapper
import net.streamok.service.document.QueryBuilder
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
                def mongo = fiberContext.dependency(MongoClient)

                Validate.notNull(collection, 'Document collection expected not to be null.')

                mongo.findWithOptions(collection, new JsonObject(new MongodbMapper().mongoQuery(queryBuilder.query).toMap()), new FindOptions().setLimit(queryBuilder.size).
                setSkip(queryBuilder.skip()).setSort(new JsonObject(new MongodbMapper().sortConditions(queryBuilder).toMap()))) {
                    def res = it.result().collect{ new MongodbMapper().mongoToCanonical(new BasicDBObject(it.map)) }
                    fiberContext.reply(Json.encode(res))
                }
            }
        }
    }

}