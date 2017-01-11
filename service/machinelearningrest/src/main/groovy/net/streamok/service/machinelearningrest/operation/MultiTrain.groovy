package net.streamok.service.machinelearningrest.operation

import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler

import static net.streamok.lib.vertx.Handlers.completeIteration

class MultiTrain implements OperationDefinition {

    public static final String machineLearningRestMultiTrain = 'machineLearningRest.multiTrain'

    @Override
    String address() {
        machineLearningRestMultiTrain
    }

    @Override
    OperationHandler handler() {
        { operation ->
            def collection = operation.nonBlankHeader('collection')
            def tags = operation.body(String[].class) as String[]
            completeIteration(tags.toList()) { iteration ->
                operation.send('machineLearning.ingestTrainingData', null, ['source': "twitter:${iteration.element()}", 'collection': collection], Void.class) {
                    iteration.ifFinished {
                        operation.send('machineLearning.trainTextLabelModel', null, ['dataset': collection], Void.class) {
                            operation.reply(null)
                        }
                    }
                }
            }
        }
    }
}
