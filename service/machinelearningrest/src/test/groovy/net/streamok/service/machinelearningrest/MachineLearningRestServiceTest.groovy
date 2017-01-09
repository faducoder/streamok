/**
 * Licensed to the Streamok under one or more
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
package net.streamok.service.machinelearningrest

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultServicesNode
import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.lib.mongo.EmbeddedMongo
import net.streamok.service.document.DocumentService
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.CountDownLatch

import static io.vertx.core.json.Json.decodeValue
import static io.vertx.core.json.Json.encode
import static java.util.UUID.randomUUID
import static java.util.concurrent.TimeUnit.SECONDS
import static net.streamok.lib.vertx.EventBuses.headers
import static net.streamok.service.document.operations.DocumentSave.documentSave
import static net.streamok.service.machinelearningrest.operation.CreateLabelContent.machineLearningRestCreateLabelContent
import static net.streamok.service.machinelearningrest.operation.GetLabelForContent.machineLearningRestGetLabelForContent
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class MachineLearningRestServiceTest {

    static def mongo = new EmbeddedMongo().start()

    static
    def bus = new DefaultServicesNode().addSuite(new MachineLearningRestService()).addSuite(new DocumentService()).vertx().eventBus()

    def dataset = randomUUID().toString()

    // Tests

    @Test
    void shouldFindLabelsForContent(TestContext context) {
        // Given
        def async = context.async()
        def testDocument = [id: '1', text: 'foo bar', labels: [iot: 50, vr: 70, cats: 99]]
        def semaphore = new CountDownLatch(1)
        bus.send(documentSave, encode(testDocument), headers(collection: "ml_content_text_${dataset}")) {
            semaphore.countDown()
        }
        semaphore.await(5, SECONDS)

        // When
        bus.send(machineLearningRestGetLabelForContent, null, headers([id: '1', collection: "${dataset}"])) {
            def result = decodeValue(it.result().body().toString(), List)
            assertThat(result.size()).isEqualTo(2)
            assertThat(result).endsWith('cats', 'vr')
            async.complete()
        }
    }

    @Test
    void shouldFindTopNLabelsForContent(TestContext context) {
        // Given
        def async = context.async()
        def testDocument = [id: '2', text: 'foo bar', labels: [iot: 50, vr: 70, cats: 99]]
        def semaphore = new CountDownLatch(1)
        bus.send(documentSave, encode(testDocument), headers(collection: "ml_content_text_${dataset}")) {
            semaphore.countDown()
        }
        semaphore.await(5, SECONDS)

        // When
        bus.send(machineLearningRestGetLabelForContent, null, headers([id: '2', collection: "${dataset}", top: "1"])) {
            def result = decodeValue(it.result().body().toString(), List)
            assertThat(result.size()).isEqualTo(1)
            assertThat(result).endsWith('cats')
            async.complete()
        }
    }

    @Test
    void shouldCreateLabelsForContent(TestContext context) {
        // Given
        def async = context.async()
        def innerBus = new DefaultServicesNode().
                addFiber(new MockMachineLearningLabelTextContent()).
                addSuite(new MachineLearningRestService()).
                addSuite(new DocumentService()).
                vertx().eventBus()
        def testDocument = [text: 'foo bar']

        // When
        def semaphore = new CountDownLatch(1)
        innerBus.send(machineLearningRestCreateLabelContent, encode(testDocument), headers(id: 3, collection: "${dataset}")) {
            semaphore.countDown()
        }
        semaphore.await(5, SECONDS)

        // Then
        innerBus.consumer('test.address', {
            assertThat(it.headers().get('id')).isEqualTo('3')
            assertThat(it.headers().get('collection')).isEqualTo(dataset)
            async.complete()
        })
    }

    class MockMachineLearningLabelTextContent implements OperationDefinition {

        @Override
        String address() {
            'machineLearning.labelTextContent'
        }

        @Override
        OperationHandler handler() {
            { OperationContext operation ->
                def contentId = operation.nonBlankHeader('id')
                def collection = operation.nonBlankHeader('collection')
                operation.vertx().eventBus().send('test.address', null, new DeliveryOptions().addHeader('id', contentId).addHeader('collection', collection))
            }
        }
    }
}
