package net.streamok.fiber.node

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberNode

class RestEndpoint implements Endpoint {

    @Override
    void connect(FiberNode fiberNode) {
        def vertx = fiberNode.vertx()
        def server = vertx.createHttpServer()

        server.requestHandler { request ->
            def address = request.uri().substring(1).replaceAll('/', '.')
            if(address.indexOf('?') != -1) {
                address = address.substring(0, address.indexOf('?'))
            }
            def dd = new DeliveryOptions()
            request.params().entries().each { dd.addHeader(it.key, it.value) }
            request.bodyHandler {
                vertx.eventBus().send(address, request.method() == HttpMethod.GET ? null : it.toString(), dd) {
                    HttpServerResponse response = request.response()
                    response.putHeader("content-type", "text/plain")
                    if(it.failed()) {
                        response.end(it.cause().message)
                    } else {
                        response.end(it.result().body().toString())
                    }
                }
            }
        }

        server.listen(8080)
    }

}