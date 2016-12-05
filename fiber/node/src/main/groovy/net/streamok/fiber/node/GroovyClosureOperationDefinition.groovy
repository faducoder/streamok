package net.streamok.fiber.node

import net.streamok.fiber.node.api.OperationHandler
import net.streamok.fiber.node.api.OperationDefinition

class GroovyClosureOperationDefinition implements OperationDefinition {

    String address

    Closure closure

    GroovyClosureOperationDefinition(String address, Closure closure) {
        this.address = address
        this.closure = closure
    }

    static GroovyClosureOperationDefinition groovyClosureFiberDefinition(String address, String definition) {
        def closure = new GroovyShell().evaluate(definition) as Closure
        new GroovyClosureOperationDefinition(address, closure)
    }

    @Override
    String address() {
        address
    }

    @Override
    OperationHandler handler() {
        closure
    }

}
