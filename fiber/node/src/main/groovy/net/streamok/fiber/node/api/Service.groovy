package net.streamok.fiber.node.api

interface Service {

    List<OperationDefinition> fiberDefinitions()

    List<DependencyProvider> dependencyProviders()

    List<Endpoint> endpoints()

}
