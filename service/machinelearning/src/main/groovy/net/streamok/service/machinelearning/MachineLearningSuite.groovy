package net.streamok.service.machinelearning

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberDefinition
import net.streamok.fiber.node.api.FiberSuite

class MachineLearningSuite implements FiberSuite {

    @Override
    List<FiberDefinition> fiberDefinitions() {
        [new MachineLearningTrain(), new MachineLearningPredict()]
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