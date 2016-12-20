package net.streamok.service.speech

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.Service
import net.streamok.service.speech.operation.Recognize

class SpeechService implements Service {

    @Override
    List<OperationDefinition> operations() {
        [new Recognize()]
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