package net.streamok.service.setup

import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler

class TagLoadingOperation implements OperationDefinition {

    // TODO - load from Mongo (Document read all )
    private final List<String> tags = ['food', 'health'] // TODO - need to HTML exscape ? what about spaces 'machine learning', 'paralel comutation'
    private final String DEFAULT_SOURCE = 'twitter'
    private final String DEFAULT_COLLECTION = 'testing' // TODO, load from config or sth
    @Override
    String address() {
        return 'loadTags'
    }

    @Override
    OperationHandler handler() {
        return { context ->
            tags.collect { createTag(it, context) }
            teachMachine(context)
        }
    }

    private

    def createTag(String tag, OperationContext context){
        def header = [
                'source': "$DEFAULT_SOURCE:$tag",
                'collection': DEFAULT_COLLECTION
        ]
        context.send('machineLearning.ingestTrainingData', null, header)
    }

    def teachMachine(OperationContext context){
        context.send('machineLearning.trainTextLabelModel', null, ['dataset': DEFAULT_COLLECTION])
    }
}
