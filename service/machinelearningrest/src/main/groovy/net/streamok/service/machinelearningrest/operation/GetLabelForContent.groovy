package net.streamok.service.machinelearningrest.operation

import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler

import static io.vertx.core.json.Json.encode
import static net.streamok.lib.conf.Conf.configuration

class GetLabelForContent implements OperationDefinition {

    static final String machineLearningRestGetLabelForContent = 'machineLearningRest.getLabelForContent'

    Integer threshold = configuration().get().getInt('machinelearning.threshold', 70)

    @Override
    String address() {
        machineLearningRestGetLabelForContent
    }

    @Override
    OperationHandler handler() {
        { OperationContext operation ->
            def contentId = operation.nonBlankHeader('id')
            def collection = operation.nonBlankHeader('collection')
            def top = operation.header('top') as Integer

            operation.send('document.findOne', null, [collection: "ml_content_text_${collection}", id: contentId], Map.class) {
                if (it.containsKey('labels')) {
                    operation.reply(encode(parseLabels(it['labels'] as Map, top)))
                } else {
                    operation.fail(404, "Not found")
                }
            }
        }
    }

    private List<String> parseLabels(Map<String, Integer> labels, Integer top) {
        def sorted = labels.sort { a, b -> b.value.compareTo(a.value) }
        sorted = sorted.findAll { it.value >= threshold }
        if (top != null) {
            sorted.collect { it.key }.take(top)
        } else {
            sorted.collect { it.key }
        }
    }
}
