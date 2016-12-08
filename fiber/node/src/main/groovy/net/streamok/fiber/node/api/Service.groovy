package net.streamok.fiber.node.api

interface Service {

    List<OperationDefinition> operations()

    List<DependencyProvider> dependencies()

    List<Endpoint> endpoints()

}
