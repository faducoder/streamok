package net.streamok.service.machinelearning

import org.apache.spark.ml.classification.LogisticRegressionModel

class ModelCache {

    private final Map<String, LogisticRegressionModel> models = [:]

    void updateModel(String collection, String label, LogisticRegressionModel model) {
        models[key(collection, label)] = model
    }

    LogisticRegressionModel model(String collection, String label) {
        models[key(collection, label)]
    }

    private def key = {String collection, String label -> "${collection}_${label}" }

}
