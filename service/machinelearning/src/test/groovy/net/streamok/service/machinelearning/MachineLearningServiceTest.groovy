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
import net.streamok.fiber.node.DefaultServicesNode
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
import static net.streamok.service.machinelearning.operation.textlabel.LabelAllTextContent.labelAllTextContent
import static net.streamok.service.machinelearning.operation.textlabel.PredictTextLabel.predictTextLabel
import static net.streamok.service.machinelearning.operation.textlabel.TrainTextLabelModel.trainTextLabelModel
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class MachineLearningServiceTest {

    static def mongo = new EmbeddedMongo().start()

    static
    def bus = new DefaultServicesNode().addSuite(new MachineLearningService()).addSuite(new DocumentService()).vertx().eventBus()

    def dataset = randomUUID().toString()

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
            bus.send(documentSave, encode(it), headers(collection: "training_texts_${dataset}")) {
                semaphore.countDown()
            }
        }
        semaphore.await(15, SECONDS)

        bus.send(trainTextLabelModel, null, headers(dataset: dataset)) {
            bus.send(predictTextLabel, encode(new TextLabelFeatureVector(text: 'I love Logistic regression')), headers(dataset: dataset)) {
                def result = decodeValue(it.result().body().toString(), Map)
                assertThat(result['default'] as int).isGreaterThan(80)
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
            bus.send('document.save', encode(it), headers(collection: 'training_texts_' + dataset))
        }
        Thread.sleep(5000)
        bus.send(trainTextLabelModel, null, headers(dataset: dataset)) {
            bus.send(predictTextLabel, encode(new TextLabelFeatureVector(text: 'This text contains some foo and lorem')), headers(dataset: dataset)) {
                def result = decodeValue(it.result().body().toString(), Map)
                assertThat(result['foo'] as int).isGreaterThan(90)
                assertThat(result['lorem'] as int).isGreaterThan(90)
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
        bus.send(trainTextLabelModel, null, new DeliveryOptions().addHeader('dataset', 'col3')) {
            bus.send(predictTextLabel, encode(new TextLabelFeatureVector(text: 'I love Logistic regression')), new DeliveryOptions().addHeader('dataset', 'col3')) {
                def result = decodeValue(it.result().body().toString(), Map)
                assertThat(result['foo'] as int).isLessThan(70)
                assertThat(result['lorem'] as int).isLessThan(70)
                async.complete()
            }
        }
    }

    @Test
    void shouldLoadTwitter(TestContext context) {
        def async = context.async()
        bus.send('machineLearning.ingestTrainingData', null, new DeliveryOptions().addHeader('source', 'twitter:iot').addHeader('collection', dataset)) {
            bus.send(trainTextLabelModel, null, headers(dataset: dataset)) {
                bus.send(predictTextLabel, encode(textFeatureVector('internet of things, cloud solutions and connected devices', true)), new DeliveryOptions().addHeader('dataset', dataset)) {
                    assertThat((decodeValue(it.result().body().toString(), Map).iot as int)).isGreaterThan(0)
                    bus.send(predictTextLabel, encode(textFeatureVector('cat and dogs are nice animals but smells nasty', true)), new DeliveryOptions().addHeader('dataset', dataset)) {
                        assertThat((decodeValue(it.result().body().toString(), Map).iot as int)).isGreaterThan(0)
                        async.complete()
                    }
                }
            }
        }
    }

    @Test
    void shouldLabelAllContext(TestContext context) {
        def async = context.async()
        bus.send('document.save', encode([text: 'internet of things, cloud solutions and connected devices']), headers(collection: "ml_content_text_${dataset}")) {
            bus.send('document.save', encode([text: 'cat and dogs are nice animals but smells nasty']), headers(collection: "ml_content_text_${dataset}")) {
                bus.send('machineLearning.ingestTrainingData', null, new DeliveryOptions().addHeader('source', 'twitter:iot').addHeader('collection', dataset)) {
                    bus.send(trainTextLabelModel, null, headers(dataset: dataset)) {
                        bus.send(labelAllTextContent, null, headers(collection: dataset)) {
                            bus.send('document.find', encode([:]), headers(collection: "ml_content_text_${dataset}")) {
                                def documents = decodeValue(it.result().body() as String, Map[])
                                assertThat(documents[0].labels as Map).hasSize(1)
                                assertThat(documents[1].labels as Map).hasSize(1)
                                async.complete()
                            }
                        }
                    }
                }
            }
        }
    }

    // Decision trees

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
            bus.send(documentSave, encode(it), headers(collection: "training_decision_${dataset}")) {
                semaphore.countDown()
            }
        }
        Validate.isTrue(semaphore.await(15, SECONDS))

        bus.send('machineLearning.trainDecisionModel', null, headers(input: dataset)) {
            if (it.succeeded()) {
                assertThat(decodeValue(it.result().body() as String, double)).isNotNegative()
                bus.send('machineLearning.decide', encode([features: [200.0d, 2.0d]]), headers(input: dataset)) {
                    assertThat(decodeValue(it.result().body().toString(), double)).isEqualTo(2.0d)
                    async.complete()
                }
            }
        }
    }

}