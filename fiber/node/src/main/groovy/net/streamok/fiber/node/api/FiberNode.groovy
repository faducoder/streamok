package net.streamok.fiber.node.api

import io.vertx.core.Vertx

interface FiberNode {

    FiberNode start()

    String id()

    FiberNode addFiber(OperationDefinition fiberDefinition)

    FiberNode addEndpoint(Endpoint endpoint)

    FiberNode addSuite(Service fiberSuite)

    FiberNode addDependency(DependencyProvider dependencyProvider)

    Object dependency(String key)

    def <T> T dependency(Class<T> type)

    Vertx vertx()

}