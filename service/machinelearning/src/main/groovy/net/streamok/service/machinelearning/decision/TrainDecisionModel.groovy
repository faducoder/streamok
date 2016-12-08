package net.streamok.service.machinelearning.decision

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.service.machinelearning.ModelCache
import org.apache.commons.lang3.Validate
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.PipelineStage
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.*
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession

class TrainDecisionModel implements OperationDefinition {

    @Override
    String address() {
        'machineLearning.trainDecisionModel'
    }

    @Override
    OperationHandler handler() {
        new OperationHandler() {
            @Override
            void handle(OperationContext operation) {
                def spark = operation.dependency(SparkSession)
                def models = operation.dependency(ModelCache)
                def input = operation.nonBlankHeader('input')

                operation.vertx().eventBus().send('document.find', Json.encode([size: 2000]), new DeliveryOptions().addHeader('collection', 'training_decision_' + input)) {
                    def datax = Json.decodeValue(it.result().body().toString(), DecisionFeatureVector[]).toList()
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

// Split the data into training and test sets (30% held out for testing).
                    double[] spl = [0.8, 0.2].toArray()
                    Dataset<Row>[] splits = data.randomSplit(spl);
                    Dataset<Row> trainingData = splits[0];
                    Dataset<Row> testData = splits[1];

// Train a TrainDecisionModel model.
                    DecisionTreeClassifier dt = new DecisionTreeClassifier()
                            .setLabelCol("indexedLabel")
                            .setFeaturesCol("indexedFeatures");

// Convert indexed labels back to original labels.
                    IndexToString labelConverter = new IndexToString()
                            .setInputCol("prediction")
                            .setOutputCol("predictedLabel")
                            .setLabels(labelIndexer.labels());

// Chain indexers and tree in a Pipeline.
                    PipelineStage[] st = [labelIndexer, featureIndexer, dt, labelConverter].toArray()
                    Pipeline pipeline = new Pipeline()
                            .setStages(st);

// Train model. This also runs the indexers.
                    PipelineModel model = pipeline.fit(trainingData);

// Make predictions.
                    Dataset<Row> predictions = model.transform(testData);

// Select example rows to display.
                    predictions.select("predictedLabel", "label", "features").show(5);

// Select (prediction, true label) and compute test error.
                    MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                            .setLabelCol("indexedLabel")
                            .setPredictionCol("prediction")
                            .setMetricName("accuracy");
                    double accuracy = evaluator.evaluate(predictions);
                    System.out.println("Test Error = " + (1.0 - accuracy));
                    models.updateModel(input, 'decision', model)
                    operation.reply(Json.encode(accuracy))
                }
            }
        }
    }
}

