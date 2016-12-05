package net.streamok.service.document.operations

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.service.document.MongodbMapper
import net.streamok.service.document.QueryBuilder

import static com.google.common.base.MoreObjects.firstNonNull
import static org.slf4j.LoggerFactory.getLogger

class DocumentCount implements OperationDefinition {

    private static final LOG = getLogger(DocumentCount)

    @Override
    String address() {
        'document.count'
    }

    @Override
    OperationHandler handler() {
        { fiberContext ->
            def collection = fiberContext.nonBlankHeader('collection')
            def queryBuilder = firstNonNull(fiberContext.body(QueryBuilder), new QueryBuilder())
            LOG.debug('About to count collection {} using query: {}', collection, queryBuilder)
            def mongo = fiberContext.dependency(MongoClient)

            mongo.count(collection, new JsonObject(new MongodbMapper().mongoQuery(queryBuilder.query))) {
                fiberContext.reply(Json.encode(it.result()))
            }
        }
    }

}