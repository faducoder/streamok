package net.streamok.fiber.node.api

import io.vertx.core.Vertx

interface ServicesNode {

    ServicesNode start()

    ServicesNode close()

    String id()

    ServicesNode addFiber(OperationDefinition fiberDefinition)

    ServicesNode addEndpoint(Endpoint endpoint)

    ServicesNode addSuite(Service fiberSuite)

    ServicesNode addDependency(DependencyProvider dependencyProvider)

    Object dependency(String key)

    def <T> T dependency(Class<T> type)

    Vertx vertx()

}