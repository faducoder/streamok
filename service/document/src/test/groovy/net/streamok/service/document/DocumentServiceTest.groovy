/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.streamok.service.document

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.lib.mongo.EmbeddedMongo
import net.streamok.service.metrics.MetricsSuite
import org.junit.Test
import org.junit.runner.RunWith

import static io.vertx.core.json.Json.decodeValue
import static io.vertx.core.json.Json.encode
import static java.util.UUID.randomUUID
import static net.streamok.lib.common.Networks.findAvailableTcpPort
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class DocumentServiceTest {

    static def mongo = new EmbeddedMongo().start(findAvailableTcpPort())

    static def bus = new DefaultFiberNode().addSuite(new DocumentService()).addSuite(new MetricsSuite()).vertx().eventBus()

    def collection = randomUUID().toString()

    def document = [foo: 'bar']

    // DocumentSave tests

    @Test
    void shouldUpdate(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode(document), new DeliveryOptions().addHeader('collection', collection)) {
            def savedId = decodeValue(it.result().body().toString(), String)
            bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', savedId)) {
                def savedDocument = decodeValue(it.result().body().toString(), Map)
                savedDocument.foo = 'updated'
                bus.send('document.save', encode(savedDocument), new DeliveryOptions().addHeader('collection', collection)) {
                    bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', savedId)) {
                        def updatedDocument = decodeValue(it.result().body().toString(), Map)
                        context.assertEquals(updatedDocument.foo, 'updated')
                        async.complete()
                    }
                }
            }
        }
    }

    // DocumentFindOne tests

    @Test
    void shouldFindOne(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode(document), new DeliveryOptions().addHeader('collection', collection)) {
            def id = decodeValue(it.result().body().toString(), String)
            bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', id)) {
                def savedDocument = decodeValue(it.result().body().toString(), Map)
                context.assertEquals(savedDocument.foo, document.foo)
                async.complete()
            }
        }
    }

    @Test
    void shouldNotFindOne(TestContext context) {
        def async = context.async()
        bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', randomUUID().toString())) {
            assertThat(it.result().body()).isNull()
            async.complete()
        }
    }

    @Test
    void findOneShouldValidateNullId(TestContext context) {
        def async = context.async()
        bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection)) {
            assertThat(it.failed()).isTrue()
            assertThat(it.cause()).hasMessageContaining('null')
            async.complete()
        }
    }

    @Test
    void findOneShouldValidateBlankId(TestContext context) {
        def async = context.async()
        bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', ' ')) {
            assertThat(it.failed()).isTrue()
            assertThat(it.cause()).hasMessageContaining('blank')
            async.complete()
        }
    }

    @Test
    void findOneShouldValidateNullCollection(TestContext context) {
        def async = context.async()
        bus.send('document.findOne', null, new DeliveryOptions().addHeader('id', 'someId')) {
            assertThat(it.failed()).isTrue()
            assertThat(it.cause()).hasMessageContaining('null')
            async.complete()
        }
    }

    @Test
    void findOneShouldValidateBlankCollection(TestContext context) {
        def async = context.async()
        bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', ' ').addHeader('id', 'someId')) {
            assertThat(it.failed()).isTrue()
            assertThat(it.cause()).hasMessageContaining('blank')
            async.complete()
        }
    }

    @Test
    void findOneShouldContainsID(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            def savedId = decodeValue(it.result().body().toString(), String)
            bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', savedId)) {
                def savedDocument = decodeValue(it.result().body().toString(), Map)
                assertThat(savedDocument.id).isEqualTo(savedId)
                async.complete()
            }
        }
    }

    @Test
    void loadedDocumentShouldHasNotMongoId(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            def savedId = decodeValue(it.result().body().toString(), String)
            bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', savedId)) {
                def savedDocument = decodeValue(it.result().body().toString(), Map)
                assertThat(savedDocument._id).isNull()
                async.complete()
            }
        }
    }

    @Test
    void shouldFindMany(TestContext context) {
        def async = context.async()
        def ids = []
        bus.send('document.save', encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            ids << decodeValue(it.result().body().toString(), String)
            bus.send('document.save', encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
                ids << decodeValue(it.result().body().toString(), String)
                bus.send('document.findMany', encode(ids), new DeliveryOptions().addHeader('collection', collection)) {
                    def savedDocuments = decodeValue(it.result().body().toString(), Map[])
                    assertThat(savedDocuments.toList()).hasSize(2)
                    async.complete()
                }
            }
        }
    }

    @Test
    void shouldFindByQuery(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            bus.send('document.find', encode([query: [bar: 'baz']]), new DeliveryOptions().addHeader('collection', collection)) {
                def savedDocuments = decodeValue(it.result().body().toString(), Map[])
                assertThat(savedDocuments.toList()).hasSize(1)
                context.assertEquals(savedDocuments.first().bar, 'baz')
                async.complete()
            }
        }
    }

    // DocumentCount tests

    @Test
    void shouldCountAll(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            bus.send('document.count', null, new DeliveryOptions().addHeader('collection', collection)) {
                def count = decodeValue(it.result().body().toString(), long)
                assertThat(count).isEqualTo(1)
                async.complete()
            }
        }
    }

    @Test
    void shouldCountByQuery(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode([bar: 'baz']), new DeliveryOptions().addHeader('collection', collection)) {
            bus.send('document.count', encode([query: [bar: 'baz']]), new DeliveryOptions().addHeader('collection', collection)) {
                def count = decodeValue(it.result().body().toString(), long)
                assertThat(count).isEqualTo(1)
                async.complete()
            }
        }
    }

    // DocumentRemove tests

    @Test
    void shouldRemove(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode(document), new DeliveryOptions().addHeader('collection', collection)) {
            def savedId = it.result().body().toString()
            bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', savedId)) {
                bus.send('document.remove', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', savedId)) {
                    bus.send('document.findOne', null, new DeliveryOptions().addHeader('collection', collection).addHeader('id', savedId)) {
                        def savedDocument = it.result().body()
                        assertThat(savedDocument).isNull()
                        async.complete()
                    }
                }
            }
        }
    }

    // Metrics tests

    @Test
    void shouldReadCountMetric(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode(document), new DeliveryOptions().addHeader('collection', collection)) {
        }
        while(!async.isCompleted()) {
            bus.send('metrics.get', null, new DeliveryOptions().addHeader('key', 'service.document.count')) {
                if(decodeValue(it.result().body().toString(), long) > 0) {
                    async.complete()
                }
            }
            Thread.sleep(1000)
        }
    }

//        @Test
//        void shouldReturnEmptyList() {
//            // When
//            def invoices = documentStore.find(collection, new QueryBuilder())
//
//            // Then
//            assertThat(invoices.size()).isEqualTo(0);
//        }
//
//        @Test
//        public void shouldFindByQuery() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = [invoiceId: invoice.invoiceId]
//
//            // When
//            def invoices = documentStore.find(collection, new QueryBuilder(query))
//
//            // Then
//            assertThat(invoices.size()).isEqualTo(1)
//            assertThat(invoices.first().invoiceId).isEqualTo(invoice.invoiceId)
//        }
//
//        @Test
//        void shouldFindOneByQuery() {
//            // Given
//            def id = documentStore.save(collection, serialize(invoice))
//
//            // When
//            def invoices = documentStore.find(collection, new QueryBuilder([myid: id]))
//
//            // Then
//            assertThat(invoices).isNotEmpty()
//        }
//
//        @Test
//        public void shouldFindAllByQuery() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            documentStore.save(collection, serialize(invoice))
//
//            // When
//            def invoices = documentStore.find(collection, new QueryBuilder())
//
//            // Then
//            assertThat(invoices).hasSize(2)
//        }
//
//        @Test
//        public void shouldNotFindByQuery() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//
//            invoice.invoiceId = 'invoice001'
//            def query = new QueryBuilder([invoiceId: "randomValue"])
//
//            // When
//            def invoices = documentStore.find(collection, query)
//
//            // Then
//            assertThat(invoices).isEmpty()
//        }
//
//        @Test
//        public void shouldFindMany() {
//            // Given
//            def firstInvoice = documentStore.save(collection, serialize(invoice))
//            def secondInvoice = documentStore.save(collection, serialize(invoice))
//
//            // When
//            def invoices = documentStore.findMany(collection, [firstInvoice, secondInvoice])
//
//            // Then
//            assertThat(invoices).hasSize(2)
//            assertThat(invoices.first().myid).isEqualTo(firstInvoice)
//            assertThat(invoices.last().myid).isEqualTo(secondInvoice)
//        }
//
//        @Test
//        public void shouldNotFindMany() {
//            // When
//            def invoices = documentStore.findMany(collection, [ObjectId.get().toString(), ObjectId.get().toString()])
//
//            // Then
//            assertThat(invoices).isEmpty()
//        }
//
//        @Test
//        public void shouldCount() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//
//            // When
//            long invoices = documentStore.count(collection, new QueryBuilder())
//
//            // Then
//            assertThat(invoices).isEqualTo(1)
//        }
//
//
//        @Test
//        public void shouldFindByNestedQuery() {
//            // Given
//            String street = "someStreet";
//            invoice.address = new Invoice.Address(street: street)
//            documentStore.save(collection, serialize(invoice))
//
//            def query = new QueryBuilder([address_street: street])
//
//            // When
//            def invoices = documentStore.find(collection, query);
//
//            // Then
//            assertThat(invoices).hasSize(1)
//            assertThat(invoices.get(0).address.street).isEqualTo(street)
//        }
//
//        @Test
//        public void shouldNotFindByNestedQuery() {
//            // Given
//            String street = "someStreet";
//            invoice.address = new Invoice.Address(street: street)
//            documentStore.save(collection, serialize(invoice))
//
//            def query = new QueryBuilder([address_street: 'someRandomStreet'])
//
//            // When
//            def invoices = documentStore.find(collection, query);
//
//            // Then
//            assertThat(invoices).isEmpty()
//        }
//
//        @Test
//        void shouldReturnPageByQuery() {
//            // Given
//            def firstInvoice = documentStore.save(collection, serialize(invoice))
//            def secondInvoice = documentStore.save(collection, serialize(invoice))
//            def thirdInvoice = documentStore.save(collection, serialize(invoice))
//
//            // When
//            def firstPage = documentStore.find(collection, new QueryBuilder().page(0).size(2))
//            def secondPage = documentStore.find(collection, new QueryBuilder().page(1).size(2))
//
//            // Then
//            assertThat(firstPage).hasSize(2)
//            assertThat(secondPage).hasSize(1)
//            assertThat(firstPage.get(0).myid).isEqualTo(firstInvoice)
//            assertThat(firstPage.get(1).myid).isEqualTo(secondInvoice)
//            assertThat(secondPage.get(0).myid).isEqualTo(thirdInvoice)
//        }
//
//        @Test
//        void shouldSortDescending() {
//            // Given
//            def firstInvoice = documentStore.save(collection, serialize(invoice))
//            def secondInvoice = documentStore.save(collection, serialize(invoice))
//            def thirdInvoice = documentStore.save(collection, serialize(invoice))
//
//            // When
//            def firstPage = documentStore.find(collection, new QueryBuilder().page(0).size(2).sortAscending(false).orderBy('myid'))
//            def secondPage = documentStore.find(collection, new QueryBuilder().page(1).size(2).sortAscending(false).orderBy('myid'))
//
//            // Then
//            assertThat(firstPage).hasSize(2)
//            assertThat(secondPage).hasSize(1)
//            assertThat(firstPage.get(0).myid).isEqualTo(thirdInvoice)
//            assertThat(firstPage.get(1).myid).isEqualTo(secondInvoice)
//            assertThat(secondPage.get(0).myid).isEqualTo(firstInvoice)
//        }
//
//        @Test
//        public void shouldFindByQueryWithContains() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceIdContains: 'oo'])
//
//            // When
//            def invoices = documentStore.find(collection, query);
//
//            // Then
//            assertThat(invoices).hasSize(1);
//            assertThat(invoices.get(0).invoiceId).isEqualTo(invoice.invoiceId)
//        }
//
//        @Test
//        public void shouldNotFindByQueryWithContains() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceIdContains: 'randomString'])
//
//            // When
//            def invoices = documentStore.find(collection, query);
//
//            // Then
//            assertThat(invoices).isEmpty()
//        }
//
//        @Test
//        void shouldFindByQueryWithIn() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceIdIn: ['foo', 'bar']])
//
//            // When
//            def invoices = documentStore.find(collection, query);
//
//            // Then
//            assertThat(invoices).hasSize(1);
//            assertThat(invoices.get(0).invoiceId).isEqualTo(invoice.invoiceId)
//        }
//
//        @Test
//        void shouldNotFindByQueryWithIn() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceIdIn: ['baz', 'bar']])
//
//            // When
//            def invoices = documentStore.find(collection, query);
//
//            // Then
//            assertThat(invoices).hasSize(0);
//        }
//
//        @Test
//        void shouldFindByQueryWithNotIn() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceIdNotIn: ['baz', 'bar']])
//
//            // When
//            def invoices = documentStore.find(collection, query);
//
//            // Then
//            assertThat(invoices).hasSize(1);
//            assertThat(invoices.get(0).invoiceId).isEqualTo(invoice.invoiceId)
//        }
//
//        @Test
//        void shouldNotFindByQueryWithNotIn() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceIdNotIn: ['foo', 'bar']])
//
//            // When
//            def invoices = documentStore.find(collection, query);
//
//            // Then
//            assertThat(invoices).isEmpty()
//        }
//
//        @Test
//        void shouldFindByQueryBetweenDateRange() {
//            // Given
//            invoice.timestamp = now().toDate()
//            def todayInvoice = documentStore.save(collection, serialize(invoice))
//            invoice = new Invoice()
//            invoice.timestamp = now().minusDays(2).toDate();
//            documentStore.save(collection, serialize(invoice))
//            invoice = new Invoice();
//            invoice.timestamp = now().plusDays(2).toDate()
//            documentStore.save(collection, serialize(invoice));
//
//            def query = new QueryBuilder([timestampGreaterThanEqual: now().minusDays(1).toDate().time, timestampLessThan: now().plusDays(1).toDate().time])
//
//            // When
//            def invoices = documentStore.find(collection, query)
//
//            // Then
//            assertThat(invoices).hasSize(1);
//            assertThat(invoices.first().myid).isEqualTo(todayInvoice)
//        }
//
//        @Test
//        void shouldCountPositiveByQuery() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceId: 'foo'])
//
//            // When
//            long invoices = documentStore.count(collection, query);
//
//            // Then
//            assertThat(invoices).isEqualTo(1);
//        }
//
//        @Test
//        public void shouldCountNegativeByQuery() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceId: 'randomValue'])
//
//            // When
//            long invoices = documentStore.count(collection, query);
//
//            // Then
//            assertThat(invoices).isEqualTo(0)
//        }
//
//        @Test
//        public void shouldCountPositiveByQueryWithContains() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceIdContains: 'oo'])
//
//            // When
//            long invoices = documentStore.count(collection, query);
//
//            // Then
//            assertThat(invoices).isEqualTo(1);
//        }
//
//        @Test
//        public void shouldCountNegativeByQueryWithContains() {
//            // Given
//            documentStore.save(collection, serialize(invoice))
//            def query = new QueryBuilder([invoiceIdContains: 'invalidQuery'])
//
//            // When
//            long invoices = documentStore.count(collection, query);
//
//            // Then
//            assertThat(invoices).isEqualTo(0)
//        }
//
//        @Test
//        public void shouldRemoveDocument() {
//            // Given
//            def id = documentStore.save(collection, serialize(invoice))
//
//            // When
//            documentStore.remove(collection, id);
//
//            // Then
//            long invoices = documentStore.count(collection, new QueryBuilder());
//            assertThat(invoices).isEqualTo(0)
//        }
//
//        // Helpers
//
//        private Map<String, Object> serialize(Invoice invoice) {
//            mapper.convertValue(invoice, Map.class)
//        }
//
//
//        // Class fixtures
//
//        static class Invoice {
//
//            String myid
//
//            Date timestamp = new Date()
//
//            String invoiceId
//
//            Address address
//
//            static class Address {
//
//                String street
//
//            }
//
//        }
//
//    }
}