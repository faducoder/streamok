package net.streamok.service.machinelearning.common

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.LogisticRegressionModel

class ModelCache {

    private final Map<String, PipelineModel> models = [:]

    void updateModel(String collection, String label, PipelineModel model) {
        models[key(collection, label)] = model
    }

    PipelineModel model(String collection, String label) {
        models[key(collection, label)]
    }

    List<String> labels(String collection) {
        models.keySet().collect{ it.split('_') }.findAll{ it.first() == collection }.collect{ it.last() }
    }

    private def key = {String collection, String label -> "${collection}_${label}" }

}
