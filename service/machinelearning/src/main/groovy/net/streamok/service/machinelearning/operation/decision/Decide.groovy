package net.streamok.service.machinelearning.operation.decision

import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.service.machinelearning.common.ModelCache
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.sql.SparkSession

import static io.vertx.core.json.Json.encode

class Decide implements OperationDefinition {

    @Override
    String address() {
        'machineLearning.decide'
    }

    @Override
    OperationHandler handler() {
        { OperationContext operation ->
            def spark = operation.dependency(SparkSession)
            def models = operation.dependency(ModelCache)

            def collection = operation.header('input').toString()
            def featureVector = operation.body(DecisionFeatureVector)
            def regressionModel = models.model(collection, 'decision')
            def featuresDataFrame = spark.createDataFrame([featureVector.toLabeledPoint()], LabeledPoint)

            def predictions = regressionModel.transform(featuresDataFrame)
            def prob = predictions.collectAsList().first().getAs(7) as double

            operation.reply(encode(prob))
        }
    }

}