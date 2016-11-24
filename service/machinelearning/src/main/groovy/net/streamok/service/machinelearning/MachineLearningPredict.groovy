package net.streamok.service.machinelearning

import io.vertx.core.json.Json
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

class MachineLearningPredict implements FiberDefinition {

    @Override
    String address() {
        'machinelearning.predict'
    }

    @Override
    Fiber handler() {
        { fiberContext ->
            def collection = fiberContext.header('collection').toString()
            def featureVector = Json.decodeValue(fiberContext.body().toString(), FeatureVector)

            def ungroupedData = MachineLearningTrain.ungroupedData[collection]
            def labelConfidence = [:]
            def labels = ungroupedData.collect { it.targetLabel }.unique()
            labels.each { label ->
                if (label == null) {
                    label = 'default'
                }
                def regressionModel = MachineLearningTrain.models[label]

                def data = [RowFactory.create(100d, featureVector.text)]
                def schema = new StructType([
                        new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                        new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
                ].toArray(new StructField[0]) as StructField[]);
                def featuresDataFrame = MachineLearningTrain.spark.createDataFrame(data, schema);
                def tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
                featuresDataFrame = tokenizer.transform(featuresDataFrame);
                def hashingTF = new HashingTF()
                        .setInputCol("words")
                        .setOutputCol("rawFeatures")
                        .setNumFeatures(20)
                def featurizedData = hashingTF.transform(featuresDataFrame);

                def idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
                def idfModel = idf.fit(featurizedData);
                def rescaledData = idfModel.transform(featurizedData);

                def predictions = regressionModel.transform(rescaledData)
                def prob = predictions.collectAsList().first().getAs(6)

                labelConfidence[label] = (prob as DenseVector).values()[1]
            }

            fiberContext.reply(Json.encode(labelConfidence))
        }
    }

}