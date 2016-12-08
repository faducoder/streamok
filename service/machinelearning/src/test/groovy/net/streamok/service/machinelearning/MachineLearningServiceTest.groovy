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
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultFiberNode
import net.streamok.lib.mongo.EmbeddedMongo
import net.streamok.service.document.DocumentService
import net.streamok.service.machinelearning.operation.decision.DecisionFeatureVector
import net.streamok.service.machinelearning.operation.textlabel.TextLabelFeatureVector
import org.apache.commons.lang.Validate
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.CountDownLatch

import static io.vertx.core.json.Json.decodeValue
import static io.vertx.core.json.Json.encode
import static java.util.UUID.randomUUID
import static java.util.concurrent.TimeUnit.SECONDS
import static net.streamok.lib.vertx.EventBuses.headers
import static net.streamok.service.document.operations.DocumentSave.documentSave
import static TextLabelFeatureVector.textFeatureVector
import static net.streamok.service.machinelearning.operation.textlabel.PredictTextLabel.predictTextLabel
import static net.streamok.service.machinelearning.operation.textlabel.TrainTextLabelModel.trainTextLabelModel
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class MachineLearningServiceTest {

    static def mongo = new EmbeddedMongo().start()

    static
    def bus = new DefaultFiberNode().addSuite(new MachineLearningService()).addSuite(new DocumentService()).vertx().eventBus()

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

        bus.send(trainTextLabelModel, null, headers(input: input)) {
            bus.send(predictTextLabel, encode(new TextLabelFeatureVector(text: 'I love Logistic regression')), headers(collection: input)) {
                def result = decodeValue(it.result().body().toString(), Map)
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
                new TextLabelFeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem')
        ]
        trainingData.each {
            bus.send('document.save', encode(it), new DeliveryOptions().addHeader('collection', 'training_texts_' + 'col2'))
        }
        Thread.sleep(5000)
        bus.send(trainTextLabelModel, null, new DeliveryOptions().addHeader('input', 'col2')) {
            bus.send(predictTextLabel, encode(new TextLabelFeatureVector(text: 'This text contains some foo and lorem')), new DeliveryOptions().addHeader('collection', 'col2')) {
                def result = decodeValue(it.result().body().toString(), Map)
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
                new TextLabelFeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'Hi I heard about Spark', targetFeature: 0, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'I wish Java could use case classes', targetFeature: 0, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'foo bar baz', targetFeature: 1, targetLabel: 'foo'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem'),
                new TextLabelFeatureVector(text: 'lorem ipsum', targetFeature: 1, targetLabel: 'lorem')
        ]
        trainingData.each {
            bus.send('document.save', encode(it), new DeliveryOptions().addHeader('collection', 'training_texts_' + 'col3'))
        }
        Thread.sleep(5000)
        bus.send(trainTextLabelModel, null, new DeliveryOptions().addHeader('input', 'col3')) {
            bus.send(predictTextLabel, encode(new TextLabelFeatureVector(text: 'I love Logistic regression')), new DeliveryOptions().addHeader('collection', 'col3')) {
                def result = decodeValue(it.result().body().toString(), Map)
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
            bus.send(trainTextLabelModel, null, new DeliveryOptions().addHeader('input', input)) {
                bus.send(predictTextLabel, encode(textFeatureVector('internet of things, cloud solutions and connected devices', true)), new DeliveryOptions().addHeader('collection', input)) {
                    assertThat((decodeValue(it.result().body().toString(), Map).iot as double)).isGreaterThan(0.0d)
                    bus.send(predictTextLabel, encode(textFeatureVector('cat and dogs are nice animals but smells nasty', true)), new DeliveryOptions().addHeader('collection', input)) {
                        assertThat((decodeValue(it.result().body().toString(), Map).iot as double)).isGreaterThan(0.0d)
                        async.complete()
                    }
                }
            }
        }
    }

    @Test
    void shouldMakeDecision(TestContext context) {
        def async = context.async()
        def trainingData = [
                new DecisionFeatureVector(features: [10.0d, 1.0d], label: 0.0d),
                new DecisionFeatureVector(features: [50.0d, 1.0d], label: 1.0d),
                new DecisionFeatureVector(features: [50.0d, 1.0d], label: 1.0d),
                new DecisionFeatureVector(features: [100.0d, 1.0d], label: 1.0d),
                new DecisionFeatureVector(features: [50.0d, 2.0d], label: 2.0d),
                new DecisionFeatureVector(features: [50.0d, 2.0d], label: 2.0d),
                new DecisionFeatureVector(features: [100.0d, 2.0d], label: 2.0d),
                new DecisionFeatureVector(features: [150.0d, 1.0d], label: 2.0d),
                new DecisionFeatureVector(features: [200.0d, 1.0d], label: 2.0d),
        ]
        def semaphore = new CountDownLatch(trainingData.size())
        trainingData.each {
            bus.send(documentSave, encode(it), headers(collection: "training_decision_${input}")) {
                semaphore.countDown()
            }
        }
        Validate.isTrue(semaphore.await(15, SECONDS))

        bus.send('machineLearning.trainDecisionModel', null, headers(input: input)) {
            if (it.succeeded()) {
                assertThat(decodeValue(it.result().body() as String, double)).isNotNegative()
                bus.send('machineLearning.decide', encode([features: [200.0d, 2.0d]]), headers(input: input)) {
                    assertThat(decodeValue(it.result().body().toString(), double)).isEqualTo(2.0d)
                    async.complete()
                }
            }
        }
    }

}