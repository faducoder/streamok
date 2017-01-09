package net.streamok.service.machinelearningrest

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.Service
import net.streamok.service.machinelearningrest.operation.CreateLabelContent
import net.streamok.service.machinelearningrest.operation.GetLabelForContent

class MachineLearningRestService implements Service {
    @Override
    List<OperationDefinition> operations() {
        [new CreateLabelContent(), new GetLabelForContent()]
    }

    @Override
    List<DependencyProvider> dependencies() {
        []
    }

    @Override
    List<Endpoint> endpoints() {
        []
    }
}
