package net.streamok.service.document

import com.mongodb.Mongo
import net.streamok.fiber.node.api.DependencyProvider

import static net.streamok.lib.conf.Conf.configuration

class MongoDriverProvider implements DependencyProvider {

    @Override
    String key() {
        'mongoDriver'
    }

    @Override
    Object dependency() {
        def host = configuration().instance().getString('MONGO_SERVICE_HOST', 'localhost')
        def port = configuration().instance().getInt('MONGO_SERVICE_PORT', 27017)
        new Mongo(host, port)
    }

}