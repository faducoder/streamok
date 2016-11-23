package net.streamok.fiber.node

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerResponse

class FiberNode {

    def vertx = Vertx.vertx()

    void addFiber(FiberDefinition fiberDefinition) {
        vertx.eventBus().consumer(fiberDefinition.address(), fiberDefinition.handler())
    }

    void addRestProtocolAdapter() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler { request ->
            def address = request.uri().substring(1).replaceAll('/', '.')
            vertx.eventBus().send(address, null) {
                HttpServerResponse response = request.response()
                response.putHeader("content-type", "text/plain")
                response.end(it.result().body().toString())
            }
        }

        server.listen(8080)
    }

}