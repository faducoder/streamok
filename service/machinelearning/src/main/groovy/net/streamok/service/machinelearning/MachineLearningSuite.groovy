package net.streamok.service.machinelearning

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.Service

class MachineLearningSuite implements Service {

    @Override
    List<OperationDefinition> fiberDefinitions() {
        [new MachineLearningIngestTrainingData(), new MachineLearningTrain(), new MachineLearningPredict()]
    }

    @Override
    List<DependencyProvider> dependencyProviders() {
        [new SparkSessionProvider(), new ModelCacheProvider()]
    }

    @Override
    List<Endpoint> endpoints() {
        []
    }
}