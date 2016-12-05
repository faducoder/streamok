package net.streamok.fiber.node.api

interface OperationDefinition {

    String address()

    OperationHandler handler()

}