package net.streamok.service.machinelearningrest.operation

import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler

class CreateLabelContent implements OperationDefinition {

    static final String machineLearningRestCreateLabelContent = 'machineLearningRest.createLabelContent'

    @Override
    String address() {
        machineLearningRestCreateLabelContent
    }

    @Override
    OperationHandler handler() {
        { OperationContext operation ->
            def contentId = operation.nonBlankHeader('id')
            def collection = operation.nonBlankHeader('collection')
            def body = operation.body()
            body.id = contentId

            operation.send('document.save', body, ['id': contentId, 'collection': "ml_content_text_${collection}"], Object.class) {
                operation.send('machineLearning.labelTextContent', null, ['id': contentId, 'collection': collection])
            }

            operation.reply(null)
        }
    }
}
