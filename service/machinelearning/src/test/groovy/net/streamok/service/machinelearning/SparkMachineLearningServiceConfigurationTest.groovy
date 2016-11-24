package net.streamok.service.machinelearning

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.FiberNode
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith

import static net.streamok.service.machinelearning.FeatureVector.textFeatureVector

@RunWith(VertxUnitRunner)
class SparkMachineLearningServiceConfigurationTest {

    static def bus = new FiberNode().addSuite(new MachineLearningSuite()).vertx().eventBus()

    // Tests

    @Test
    void shouldDetectSimilarity(TestContext context) {
        // Given
        def async = context.async()
        MachineLearningTrain.ungroupedData['col1'] = [textFeatureVector('Hi I heard about Spark', false),
                textFeatureVector('I wish Java could use case classes', false),
                textFeatureVector('Logistic regression models are neat', true),
                textFeatureVector('Logistic regression models are neat', true),
                                                      textFeatureVector('Logistic regression models are neat', true),
                textFeatureVector('Logistic regression models are neat', true),
                textFeatureVector('Logistic regression models are neat', true)]
        bus.send('machinelearning.train', null, new DeliveryOptions().addHeader('collection', 'col1')) {
            bus.send('machinelearning.predict', Json.encode(new FeatureVector(text: 'I love Logistic regression')),  new DeliveryOptions().addHeader('collection', 'col1')) {
                def result = Json.decodeValue(it.result().body().toString(), Map)
                Assertions.assertThat(result['default'] as double).isGreaterThan(0.4d)
                async.complete()
            }
        }
    }

    @Test
    void shouldDetectDoubleSimilarity(TestContext context) {
        // Given
        def async = context.async()
        MachineLearningTrain.ungroupedData['col2'] =[
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
            bus.send('machinelearning.predict', Json.encode(new FeatureVector(text: 'This text contains some foo and lorem')),  new DeliveryOptions().addHeader('collection', 'col2')) {
                def result = Json.decodeValue(it.result().body().toString(), Map)
                org.assertj.core.api.Assertions.assertThat(result['foo'] as double).isGreaterThan(0.7d)
                org.assertj.core.api.Assertions.assertThat(result['lorem'] as double).isGreaterThan(0.7d)
                async.complete()
            }
        }
    }


}