package net.streamok.service.document

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.lib.conf.Conf
import net.streamok.lib.mongo.EmbeddedMongo
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(VertxUnitRunner)
class DocumentSuiteTest {

    static int mongoPort = 1024 + new Random().nextInt(10000)

    @BeforeClass
    static void beforeClass() {
        Conf.configuration().instance().addProperty('MONGO_SERVICE_PORT', mongoPort)
        bus = new DefaultFiberNode().addSuite(new DocumentStoreSuite()).vertx().eventBus()
    }

    static def mongo = new EmbeddedMongo().start(mongoPort)

    static EventBus bus

    @Test
    void shouldSave(TestContext context) {
        def async = context.async()
        bus.send('document.save', Json.encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', 'foo')) {
            bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', 'foo').addHeader('id', it.result().body().toString())) {
                def xx = Json.decodeValue(it.result().body().toString(), Map)
                context.assertEquals(xx.bar, 'baz')
                async.complete()
            }
        }
    }

}