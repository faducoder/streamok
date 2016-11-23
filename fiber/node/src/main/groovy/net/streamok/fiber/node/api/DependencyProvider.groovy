package net.streamok.fiber.node.api

interface DependencyProvider {

    String key()

    Object dependency()

}