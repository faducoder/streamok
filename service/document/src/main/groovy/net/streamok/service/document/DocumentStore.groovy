package net.streamok.service.document

import com.mongodb.Mongo
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition

import static org.slf4j.LoggerFactory.getLogger

class DocumentStore implements FiberDefinition {

    private static final LOG = getLogger(DocumentStore)

    @Override
    String address() {
        'document.save'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def pojo = fiberContext.body()
            def collection = fiberContext.header('collection').toString()
            def mongo = fiberContext.dependency(Mongo)


            LOG.debug('About to save {} into {}.', pojo, collection)

            def xxx = new MongodbMapper().canonicalToMongo(pojo)
            mongo.getDB('documents').getCollection(collection).save(xxx)
            def id = xxx['_id'].toString()
            fiberContext.reply(id)
        }
    }

}