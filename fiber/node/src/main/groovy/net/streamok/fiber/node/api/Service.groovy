package net.streamok.fiber.node.api

interface Service {

    List<FiberDefinition> fiberDefinitions()

    List<DependencyProvider> dependencyProviders()

    List<Endpoint> endpoints()

}
