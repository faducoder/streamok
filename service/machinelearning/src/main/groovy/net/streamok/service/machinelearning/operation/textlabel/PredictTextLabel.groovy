/**
 * Licensed to the Streamok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.streamok.service.machinelearning.operation.textlabel

import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.service.machinelearning.common.ModelCache
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.types.Metadata
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

import static io.vertx.core.json.Json.encode

class PredictTextLabel implements OperationDefinition {

    public static final String predictTextLabel = 'machineLearning.predictTextLabel'

    @Override
    String address() {
        predictTextLabel
    }

    @Override
    OperationHandler handler() {
        { operation ->
            def spark = operation.dependency(SparkSession)
            def models = operation.dependency(ModelCache)

            def collection = operation.nonBlankHeader('dataset')
            def featureVector = operation.body()

            def labelConfidence = [:]
            def labels = models.labels(collection)
            labels.each { label ->
                if (label == null) {
                    label = 'default'
                }
                def regressionModel = models.model(collection, label)

                def data = [RowFactory.create(100d, featureVector.text)]
                def schema = new StructType([
                        new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                        new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
                ].toArray(new StructField[0]) as StructField[]);
                def featuresDataFrame = spark.createDataFrame(data, schema)

                def predictions = regressionModel.transform(featuresDataFrame)
                def prob = predictions.collectAsList().first().getAs(5)

                labelConfidence[label] = ((prob as DenseVector).values()[1] * 100.0) as int
            }
            operation.reply(encode(labelConfidence))
        }
    }

}