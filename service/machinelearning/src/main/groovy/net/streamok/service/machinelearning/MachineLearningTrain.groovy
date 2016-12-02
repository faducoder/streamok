package net.streamok.service.machinelearning

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.Tokenizer
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

            def collection = fiber.header('collection').toString()
            def source = fiber.header('source')

            fiber.vertx().eventBus().send('document.find', Json.encode([size: 2000]), new DeliveryOptions().addHeader('collection', 'training_texts')) {
                def data = Json.decodeValue(it.result().body().toString(), FeatureVector[])

                def dataSource = new InMemoryVectorsSource(spark, data.toList())

                def labels = ['iot']
                labels.each { label ->
                    def schema = new StructType([
                            new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                            new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
                    ].toArray(new StructField[0]) as StructField[]);
                    def featuresDataFrame = spark.createDataFrame(dataSource.source(label), schema)
                    def tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
                    featuresDataFrame = tokenizer.transform(featuresDataFrame);
                    int numFeatures = 1000;
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

}
