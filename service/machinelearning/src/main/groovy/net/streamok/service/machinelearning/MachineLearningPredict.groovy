package net.streamok.service.machinelearning

import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.service.machinelearning.textlabel.TextLabelFeatureVector
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

import static io.vertx.core.json.Json.encode

class MachineLearningPredict implements OperationDefinition {

    @Override
    String address() {
        'machineLearning.predict'
    }

    @Override
    OperationHandler handler() {
        { fiber ->
            def spark = fiber.dependency(SparkSession)
            def models = fiber.dependency(ModelCache)

            def collection = fiber.header('collection').toString()
            def featureVector = fiber.body(TextLabelFeatureVector)

            def labelConfidence = [:]
            def labels = models.labels(collection)
            labels.each { label ->
                if (label == null) {
                    label = 'default'
                }
                def regressionModel = models.model(collection, label)

                def data = [RowFactory.create(100d, featureVector.text)]
                def schema = new StructType([
                        new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                        new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
                ].toArray(new StructField[0]) as StructField[]);
                def featuresDataFrame = spark.createDataFrame(data, schema)

                def predictions = regressionModel.transform(featuresDataFrame)
                def prob = predictions.collectAsList().first().getAs(5)

                labelConfidence[label] = (prob as DenseVector).values()[1]
            }

            fiber.reply(encode(labelConfidence))
        }
    }

}