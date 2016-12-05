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
package net.streamok.service.machinelearning

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.lib.mongo.EmbeddedMongo
import net.streamok.service.document.DocumentService
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.CountDownLatch

import static io.vertx.core.json.Json.encode
import static java.util.UUID.randomUUID
import static java.util.concurrent.TimeUnit.SECONDS
import static net.streamok.lib.vertx.EventBuses.headers
import static net.streamok.service.document.operations.DocumentSave.documentSave
import static net.streamok.service.machinelearning.FeatureVector.textFeatureVector
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class MachineLearningSuiteTest {

    static def mongo = new EmbeddedMongo().start()

    static
    def bus = new DefaultFiberNode().addSuite(new MachineLearningSuite()).addSuite(new DocumentService()).vertx().eventBus()

    def input = randomUUID().toString()

    // Tests

    @Test
    void shouldDetectSimilarity(TestContext context) {
        def async = context.async()
        def trainingData = [textFeatureVector('Hi I heard about Spark', false),
                            textFeatureVector('I wish Java could use case classes', false),
                            textFeatureVector('Logistic regression models are neat', true),
                            textFeatureVector('Logistic regression models are neat', true),
                            textFeatureVector('Logistic regression models are neat', true),
                            textFeatureVector('Logistic regression models are neat', true),
                            textFeatureVector('Logistic regression models are neat', true)]
        def semaphore = new CountDownLatch(trainingData.size())
        trainingData.each {
            bus.send(documentSave, encode(it), headers(collection: "training_texts_${input}")) {
                semaphore.countDown()
            }
        }
        semaphore.await(15, SECONDS)

        bus.send('machineLearning.train', null, headers(input: input)) {
            bus.send('machineLearning.predict', encode(new FeatureVector(text: 'I love Logistic regression')), headers(collection: input)) {
                def result = Json.decodeValue(it.result().body().toString(), Map)
                assertThat(result['default'] as double).isGreaterThan(0.8d)
                async.complete()
            }
        }
    }

    @Test
    void shouldDetectTwoLabels(TestContext context) {
        // Given
        def async = context.async()
        def trainingData = [
                new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'foo'),
                new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'foo'),
                new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'lorem'),
                new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'lorem'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem')
        ]
        trainingData.each {
            bus.send('document.save', encode(it), new DeliveryOptions().addHeader('collection', 'training_texts_' + 'col2'))
        }
        Thread.sleep(5000)
        bus.send('machineLearning.train', null, new DeliveryOptions().addHeader('input', 'col2')) {
            bus.send('machineLearning.predict', encode(new FeatureVector(text: 'This text contains some foo and lorem')), new DeliveryOptions().addHeader('collection', 'col2')) {
                def result = Json.decodeValue(it.result().body().toString(), Map)
                assertThat(result['foo'] as double).isGreaterThan(0.9d)
                assertThat(result['lorem'] as double).isGreaterThan(0.9d)
                async.complete()
            }
        }
    }

    @Test
    void shouldRejectRandomText(TestContext context) {
        // Given
        def async = context.async()
        def trainingData = [
                new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'foo'),
                new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'foo'),
                new FeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'lorem'),
                new FeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'lorem'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new FeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem')
        ]
        trainingData.each {
            bus.send('document.save', encode(it), new DeliveryOptions().addHeader('collection', 'training_texts_' + 'col3'))
        }
        Thread.sleep(5000)
        bus.send('machineLearning.train', null, new DeliveryOptions().addHeader('input', 'col3')) {
            bus.send('machineLearning.predict', encode(new FeatureVector(text: 'I love Logistic regression')), new DeliveryOptions().addHeader('collection', 'col3')) {
                def result = Json.decodeValue(it.result().body().toString(), Map)
                assertThat(result['foo'] as double).isLessThan(0.7d)
                assertThat(result['lorem'] as double).isLessThan(0.7d)
                async.complete()
            }
        }
    }

    @Test
    void shouldLoadTwitter(TestContext context) {
        def async = context.async()
        bus.send('machineLearning.ingestTrainingData', null, new DeliveryOptions().addHeader('source', 'twitter:iot').addHeader('collection', input)) {
            bus.send('machineLearning.train', null, new DeliveryOptions().addHeader('input', input)) {
                bus.send('machineLearning.predict', encode(textFeatureVector('internet of things, cloud solutions and connected devices', true)), new DeliveryOptions().addHeader('collection', input)) {
                    assertThat((Json.decodeValue(it.result().body().toString(), Map).iot as double)).isGreaterThan(0.0d)
                    bus.send('machineLearning.predict', encode(textFeatureVector('cat and dogs are nice animals but smells nasty', true)), new DeliveryOptions().addHeader('collection', input)) {
                        assertThat((Json.decodeValue(it.result().body().toString(), Map).iot as double)).isGreaterThan(0.0d)
                        async.complete()
                    }
                }
            }
        }
    }

}