package net.streamok.service.machinelearning.operation.textlabel

import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler
import net.streamok.lib.vertx.Handlers

import static io.vertx.core.json.Json.decodeValue
import static io.vertx.core.json.Json.encode
import static net.streamok.lib.vertx.EventBuses.headers
import static net.streamok.service.machinelearning.operation.textlabel.LabelTextContent.labelTextContent

class LabelAllTextContent implements OperationDefinition {

    public static final String labelAllTextContent = 'machineLearning.labelAllTextContent'

    @Override
    String address() {
        labelAllTextContent
    }

    @Override
    OperationHandler handler() {
        { operation ->
            def collection = operation.nonBlankHeader('collection')
            operation.vertx().eventBus().send('document.find', encode([:]), headers(collection: "ml_content_text_${collection}")) {
                List<TextLabelFeatureVector> documents = decodeValue(it.result().body() as String, TextLabelFeatureVector[]).toList()
                Handlers.completeIteration(documents) { iteration ->
                    operation.vertx().eventBus().send(labelTextContent, null, headers(collection: collection, id: iteration.element().id)) {
                        iteration.ifFinished {
                            operation.reply(null)
                        }
                    }
                }
            }
        }
    }

}