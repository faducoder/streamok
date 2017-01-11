package net.streamok.service.document.operations

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.vertx.VertxOperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.service.document.MongodbMapper
import net.streamok.service.document.QueryBuilder

import static org.slf4j.LoggerFactory.getLogger

class DocumentFind implements OperationDefinition {

    private static final LOG = getLogger(DocumentFind)

    @Override
    String address() {
        'document.find'
    }

    @Override
    OperationHandler handler() {
        { operation ->
            def collection = operation.nonBlankHeader('collection')
            def queryBuilder = operation.body(QueryBuilder) ?: new QueryBuilder()
            def mongo = operation.dependency(MongoClient)

            mongo.findWithOptions(collection, new JsonObject(new MongodbMapper().mongoQuery(queryBuilder.query)), new FindOptions().setLimit(queryBuilder.size).
                    setSkip(queryBuilder.skip()).setSort(new JsonObject(new MongodbMapper().sortConditions(queryBuilder)))) {
                def res = it.result().collect { new MongodbMapper().mongoToCanonical(it.map) }
                operation.reply(Json.encode(res))
            }
        }
    }

}