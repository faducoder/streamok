package net.streamok.service.machinelearning

import net.streamok.fiber.node.api.DependencyProvider
import org.apache.spark.sql.SparkSession

class SparkSessionProvider implements DependencyProvider {

    @Override
    String key() {
        'spark'
    }

    @Override
    Object dependency() {
        SparkSession.builder().master("local[*]").getOrCreate()
    }

}