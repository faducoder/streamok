package net.streamok.fiber.node

import io.vertx.core.json.Json
import net.streamok.fiber.node.api.OperationDefinition

class FiberDefinitionFactory {

    OperationDefinition build(String definition) {
        build(Json.decodeValue(definition, Map))
    }

    OperationDefinition build(Map<String, Object> definition) {
        if(definition.type == 'groovy') {
            return GroovyClosureOperationDefinition.groovyClosureFiberDefinition(definition.address as String, definition.closure as String)
        }
        null
    }

}
