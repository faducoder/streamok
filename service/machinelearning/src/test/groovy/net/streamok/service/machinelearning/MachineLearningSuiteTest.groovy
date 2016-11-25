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
import org.junit.Test
import org.junit.runner.RunWith

import static net.streamok.service.machinelearning.FeatureVector.textFeatureVector
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class MachineLearningSuiteTest {

    static def bus = new DefaultFiberNode().addSuite(new MachineLearningSuite()).vertx().eventBus()

    // Tests

    @Test
    void shouldDetectSimilarity(TestContext context) {
        def async = context.async()
        MachineLearningTrain.ungroupedData['col1'] = [
                textFeatureVector('Hi I heard about Spark', false),
                textFeatureVector('I wish Java could use case classes', false),
                textFeatureVector('Logistic regression models are neat', true),
                textFeatureVector('Logistic regression models are neat', true),
                textFeatureVector('Logistic regression models are neat', true),
                textFeatureVector('Logistic regression models are neat', true),
                textFeatureVector('Logistic regression models are neat', true)]

        bus.send('machinelearning.train', null, new DeliveryOptions().addHeader('collection', 'col1')) {
            bus.send('machinelearning.predict', Json.encode(new FeatureVector(text: 'I love Logistic regression')), new DeliveryOptions().addHeader('collection', 'col1')) {
                def result = Json.decodeValue(it.result().body().toString(), Map)
                assertThat(result['default'] as double).isGreaterThan(0.4d)
                async.complete()
            }
        }
    }

    @Test
    void shouldDetectDoubleSimilarity(TestContext context) {
        // Given
        def async = context.async()
        MachineLearningTrain.ungroupedData['col2'] = [
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

        bus.send('machinelearning.train', null, new DeliveryOptions().addHeader('collection', 'col2')) {
            bus.send('machinelearning.predict', Json.encode(new FeatureVector(text: 'This text contains some foo and lorem')), new DeliveryOptions().addHeader('collection', 'col2')) {
                def result = Json.decodeValue(it.result().body().toString(), Map)
                assertThat(result['foo'] as double).isGreaterThan(0.7d)
                assertThat(result['lorem'] as double).isGreaterThan(0.7d)
                async.complete()
            }
        }
    }

}