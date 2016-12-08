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
package net.streamok.service.machinelearning.decision

import io.vertx.core.eventbus.DeliveryOptions
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.service.machinelearning.ModelCache
import org.apache.commons.lang3.Validate
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.PipelineStage
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.*
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession

import static io.vertx.core.json.Json.decodeValue
import static io.vertx.core.json.Json.encode

class TrainDecisionModel implements OperationDefinition {

    @Override
    String address() {
        'machineLearning.trainDecisionModel'
    }

    @Override
    OperationHandler handler() {
        { operation ->
            def spark = operation.dependency(SparkSession)
            def models = operation.dependency(ModelCache)
            def input = operation.nonBlankHeader('input')
            operation.vertx().eventBus().send('document.find', encode([size: 2000]), new DeliveryOptions().addHeader('collection', 'training_decision_' + input)) {
                if(it.succeeded()) {
                    def datax = decodeValue(it.result().body().toString(), DecisionFeatureVector[]).toList()
                    Validate.notEmpty(datax, "Training data can't be empty.")

                    List<LabeledPoint> labeledPoints = datax.collect { it.toLabeledPoint() }

                    Dataset<Row> data = spark.createDataFrame(labeledPoints, LabeledPoint)

                    StringIndexerModel labelIndexer = new StringIndexer()
                            .setInputCol("label")
                            .setOutputCol("indexedLabel")
                            .fit(data);

                    VectorIndexerModel featureIndexer = new VectorIndexer()
                            .setInputCol("features")
                            .setOutputCol("indexedFeatures")
                            .setMaxCategories(4) // features with > 4 distinct values are treated as continuous.
                            .fit(data);

                    double[] spl = [0.8, 0.2].toArray()
                    Dataset<Row>[] splits = data.randomSplit(spl);
                    Dataset<Row> trainingData = splits[0];
                    Dataset<Row> testData = splits[1];

                    def decisionTree = new DecisionTreeClassifier()
                            .setLabelCol("indexedLabel")
                            .setFeaturesCol("indexedFeatures");

                    IndexToString labelConverter = new IndexToString()
                            .setInputCol("prediction")
                            .setOutputCol("predictedLabel")
                            .setLabels(labelIndexer.labels());

// Chain indexers and tree in a Pipeline.
                    PipelineStage[] st = [labelIndexer, featureIndexer, decisionTree, labelConverter].toArray()
                    Pipeline pipeline = new Pipeline()
                            .setStages(st);
                    def model = pipeline.fit(trainingData)
                    Dataset<Row> predictions = model.transform(testData);

// Select example rows to display.
                    predictions.select("predictedLabel", "label", "features").show(5);

// Select (prediction, true label) and compute test error.
                    MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                            .setLabelCol("indexedLabel")
                            .setPredictionCol("prediction")
                            .setMetricName("accuracy");
                    double accuracy = evaluator.evaluate(predictions);
                    models.updateModel(input, 'decision', model)
                    operation.reply(encode(accuracy))
                } else {
                    operation.fail(100, it.cause().message)
                }
            }
        }
    }
}

