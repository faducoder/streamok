package net.streamok.service.machinelearning.operation.textlabel

import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.service.machinelearning.common.ModelCache
import net.streamok.service.machinelearning.operation.textlabel.TextLabelFeatureVector
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

import static io.vertx.core.json.Json.encode

class PredictTextLabel implements OperationDefinition {

    public static final String predictTextLabel = 'machineLearning.predictTextLabel'

    @Override
    String address() {
        predictTextLabel
    }

    @Override
    OperationHandler handler() {
        { operation ->
            def spark = operation.dependency(SparkSession)
            def models = operation.dependency(ModelCache)

            def collection = operation.header('collection').toString()
            def featureVector = operation.body(TextLabelFeatureVector)

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

            operation.reply(encode(labelConfidence))
        }
    }

}