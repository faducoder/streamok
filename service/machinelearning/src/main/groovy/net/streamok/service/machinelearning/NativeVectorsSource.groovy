package net.streamok.service.machinelearning

import org.apache.spark.api.java.JavaRDD
import org.apache.spark.sql.Row

interface NativeVectorsSource {

    List<String> labels()

    JavaRDD<Row> source(String label)

}