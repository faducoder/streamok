package net.streamok.service.machinelearning

import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

class MachineLearningTrain implements FiberDefinition {

    static Map<String, List<FeatureVector>> ungroupedData = [:].withDefault {[]}

    @Override
    String address() {
        'machinelearning.train'
    }

    @Override
    Fiber handler() {
        { fiber ->
            def spark = fiber.dependency(SparkSession)
            def models = fiber.dependency(ModelCache)

            def source = fiber.header('source')
            def collection = fiber.header('collection').toString()

            def labels = ungroupedData[collection].collect { it.targetLabel }.unique()
            labels.each { label ->
                def data = ungroupedData[collection].findAll { it.targetLabel == label }.collect {
                    RowFactory.create(it.targetFeature, it.text)
                }
                if (data.isEmpty()) {
                    throw new IllegalStateException()
                }

                def schema = new StructType([
                        new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                        new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
                ].toArray(new StructField[0]) as StructField[]);
                def featuresDataFrame = spark.createDataFrame(data, schema)
                def tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
                featuresDataFrame = tokenizer.transform(featuresDataFrame);
                int numFeatures = 20;
                def hashingTF = new HashingTF()
                        .setInputCol("words")
                        .setOutputCol("rawFeatures")
                        .setNumFeatures(numFeatures);
                def featurizedData = hashingTF.transform(featuresDataFrame)
                def idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
                def idfModel = idf.fit(featurizedData)
                def rescaledData = idfModel.transform(featurizedData);

                if (label == null) {
                    label = 'default'
                }
                models.updateModel(collection, label, new LogisticRegression().fit(rescaledData))
            }
            fiber.reply(null)
        }
    }

}
