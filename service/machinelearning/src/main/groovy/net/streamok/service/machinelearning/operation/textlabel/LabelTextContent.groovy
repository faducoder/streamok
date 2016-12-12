package net.streamok.service.machinelearning.operation.textlabel

import io.vertx.core.json.Json
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler

import static io.vertx.core.json.Json.encode
import static net.streamok.lib.vertx.EventBuses.headers
import static net.streamok.service.machinelearning.operation.textlabel.PredictTextLabel.predictTextLabel

class LabelTextContent implements OperationDefinition {

    public static final String labelTextContent = 'machineLearning.labelTextContent'

    @Override
    String address() {
        labelTextContent
    }

    @Override
    OperationHandler handler() {
        { operation ->
            def collection = operation.nonBlankHeader('collection')
            def contentId = operation.nonBlankHeader('id')
            operation.vertx().eventBus().send('document.findOne', null, headers(collection: "ml_content_text_${collection}", id: contentId)) {
                def document = Json.decodeValue(it.result().body() as String, Map)
                operation.vertx().eventBus().send(predictTextLabel, encode(document), headers(collection: collection)) {
                    def labels = Json.decodeValue(it.result().body() as String, Map)
                    document.labels = labels
                    operation.vertx().eventBus().send('document.save', encode(document), headers(collection: "ml_content_text_${collection}")) {
                        operation.reply(null)
                    }
                }
            }
        }
    }

}