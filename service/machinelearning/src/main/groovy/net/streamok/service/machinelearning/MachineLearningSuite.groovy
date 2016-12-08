package net.streamok.service.machinelearning

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.Service
import net.streamok.service.machinelearning.decision.Decide
import net.streamok.service.machinelearning.decision.TrainDecisionModel
import net.streamok.service.machinelearning.textlabel.TrainTextLabelModel

class MachineLearningSuite implements Service {

    @Override
    List<OperationDefinition> fiberDefinitions() {
        [new MachineLearningIngestTrainingData(), new TrainTextLabelModel(), new MachineLearningPredict(),
         new TrainDecisionModel(), new Decide()]
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