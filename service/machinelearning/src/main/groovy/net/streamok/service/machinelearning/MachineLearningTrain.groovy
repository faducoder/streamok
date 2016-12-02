package net.streamok.service.machinelearning

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import org.apache.commons.lang3.Validate
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.PipelineStage
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

class MachineLearningTrain implements FiberDefinition {

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

            fiber.vertx().eventBus().send('document.find', Json.encode([size: 2000]), new DeliveryOptions().addHeader('collection', 'training_texts_' + collection)) {
                def data = Json.decodeValue(it.result().body().toString(), FeatureVector[]).toList()
                Validate.notEmpty(data, "Training data can't be empty.")

                def dataSource = new InMemoryVectorsSource(spark, data)

                def labels = dataSource.labels()
                labels.each { label ->
                    def schema = new StructType([
                            new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                            new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
                    ].toArray(new StructField[0]) as StructField[]);
                    def featuresDataFrame = spark.createDataFrame(dataSource.source(label), schema)
//                    def tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
//                    featuresDataFrame = tokenizer.transform(featuresDataFrame);
//                    int numFeatures = 1000;
//                    def hashingTF = new HashingTF()
//                            .setInputCol("words")
//                            .setOutputCol("rawFeatures")
//                            .setNumFeatures(numFeatures);
//                    def featurizedData = hashingTF.transform(featuresDataFrame)
//                    def idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
//                    def idfModel = idf.fit(featurizedData)
//                    def rescaledData = idfModel.transform(featurizedData);

                    def tokenizer = new Tokenizer()
                            .setInputCol("sentence")
                            .setOutputCol("words")
                    def hashingTF = new HashingTF()
                            .setNumFeatures(1000)
                            .setInputCol(tokenizer.getOutputCol())
                            .setOutputCol("features")
                    def lr = new LogisticRegression()
                            .setMaxIter(10)
                            .setRegParam(0.01)
                    PipelineStage[] st =  [tokenizer, hashingTF, lr].toArray()
                    def pipeline = new Pipeline()
                            .setStages(st)

                    if (label == null) {
                        label = 'default'
                    }
                    models.updateModel(collection, label, pipeline.fit(featuresDataFrame))
                }
                fiber.reply(null)
            }
        }
    }

}
