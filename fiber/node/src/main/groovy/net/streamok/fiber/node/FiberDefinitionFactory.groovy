package net.streamok.fiber.node

import io.vertx.core.json.Json

class FiberDefinitionFactory {

    FiberDefinition build(String definition) {
        build(Json.decodeValue(definition, Map))
    }

    FiberDefinition build(Map<String, Object> definition) {
        if(definition.type == 'groovy') {
            return GroovyClosureFiberDefinition.groovyClosureFiberDefinition(definition.address as String, definition.closure as String)
        }
        null
    }

}
