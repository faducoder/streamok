package net.streamok.service.machinelearning.decision

import io.vertx.core.json.Json
import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.service.machinelearning.FeatureVector
import net.streamok.service.machinelearning.ModelCache
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

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