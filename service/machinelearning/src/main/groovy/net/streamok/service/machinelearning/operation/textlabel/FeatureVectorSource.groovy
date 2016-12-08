package net.streamok.service.machinelearning.operation.textlabel

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.sql.Row

interface FeatureVectorSource {

    List<String> labels()

    JavaRDD<Row> source(String label)

}