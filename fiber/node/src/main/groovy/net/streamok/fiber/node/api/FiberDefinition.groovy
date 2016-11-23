package net.streamok.fiber.node.api

interface FiberDefinition {

    String address()

    Fiber handler()

}