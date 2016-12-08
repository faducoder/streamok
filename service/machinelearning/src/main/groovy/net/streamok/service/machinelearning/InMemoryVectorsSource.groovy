package net.streamok.service.machinelearning

import net.streamok.service.machinelearning.textlabel.TextLabelFeatureVector
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.Row
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession

class InMemoryVectorsSource implements NativeVectorsSource {

    SparkSession sparkSession

    List<TextLabelFeatureVector> vectors

    InMemoryVectorsSource(SparkSession sparkSession, List<TextLabelFeatureVector> vectors) {
        this.sparkSession = sparkSession
        this.vectors = vectors
    }

    @Override
    List<String> labels() {
        vectors.collect { it.targetLabel }.unique()
    }

    @Override
    JavaRDD<Row> source(String label) {
        def labelData = vectors.findAll { it.targetLabel == label }.collect{ RowFactory.create(it.targetFeature, it.text) }
        new JavaSparkContext(sparkSession.sparkContext()).parallelize(labelData)
    }

}