package net.streamok.fiber.node.api

interface FiberSuite {

    List<FiberDefinition> fiberDefinitions()

    List<DependencyProvider> dependencyProviders()

}
