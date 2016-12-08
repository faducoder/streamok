package net.streamok.service.machinelearning.decision

import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.Vectors

class DecisionFeatureVector {

    String id

    List<Double> features

    double label

    LabeledPoint toLabeledPoint() {
        new LabeledPoint(label, Vectors.dense(features.toArray() as double[]))
    }

}
