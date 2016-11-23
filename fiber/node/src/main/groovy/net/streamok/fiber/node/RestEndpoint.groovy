package net.streamok.fiber.node

import io.vertx.core.http.HttpServerResponse

class RestEndpoint implements Endpoint {

    @Override
    void connect(FiberNode fiberNode) {
        def vertx = fiberNode.vertx
        def server = vertx.createHttpServer()

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