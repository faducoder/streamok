package net.streamok.service.document

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.lib.mongo.EmbeddedMongo
import org.junit.Test
import org.junit.runner.RunWith

import static java.util.UUID.randomUUID
import static net.streamok.lib.common.Networks.findAvailableTcpPort
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class DocumentSuiteTest {

    static def mongo = new EmbeddedMongo().start(findAvailableTcpPort())

    static def bus = new DefaultFiberNode().addSuite(new DocumentStoreSuite()).vertx().eventBus()

    def collection = randomUUID().toString()

    // Tests

    @Test
    void shouldSave(TestContext context) {
        def async = context.async()
        bus.send('document.save', Json.encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', it.result().body().toString())) {
                def savedDocument = Json.decodeValue(it.result().body().toString(), Map)
                context.assertEquals(savedDocument.bar, 'baz')
                async.complete()
            }
        }
    }

    @Test
    void shouldFindMany(TestContext context) {
        def async = context.async()
        def ids = []
        bus.send('document.save', Json.encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            ids << it.result().body().toString()
            bus.send('document.save', Json.encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
                ids << it.result().body().toString()
                bus.send('document.findMany', Json.encode(ids), new DeliveryOptions().addHeader('collection', collection)) {
                    def savedDocuments = Json.decodeValue(it.result().body().toString(), Map[])
                    assertThat(savedDocuments.toList()).hasSize(2)
                    async.complete()
                }
            }
        }
    }

    @Test
    void shouldFindByQuery(TestContext context) {
        def async = context.async()
        bus.send('document.save', Json.encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            bus.send('document.find', Json.encode([query: [bar: 'baz']]), new DeliveryOptions().addHeader('collection', collection)) {
                def savedDocuments = Json.decodeValue(it.result().body().toString(), Map[])
                assertThat(savedDocuments.toList()).hasSize(1)
                context.assertEquals(savedDocuments.first().bar, 'baz')
                async.complete()
            }
        }
    }

}