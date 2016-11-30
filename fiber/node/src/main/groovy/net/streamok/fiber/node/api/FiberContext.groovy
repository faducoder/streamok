package net.streamok.fiber.node.api

import io.vertx.core.eventbus.Message

import static io.vertx.core.json.Json.decodeValue

class FiberContext {

    private final Message message

    private final FiberNode fiberNode

    FiberContext(Message message, FiberNode fiberNode) {
        this.message = message
        this.fiberNode = fiberNode
    }

    Map body() {
        body(Map)
    }

    def <T> T body(Class<T> type) {
        def json = message.body() as String
        if(json == null) {
            return null
        }
        decodeValue(json, type)
    }

    Object header(String name) {
        def matchingHeaders = message.headers().getAll(name)
        matchingHeaders.isEmpty() ? null : matchingHeaders.first()
    }

    void reply(Object payload) {
        message.reply(payload)
    }

    def fail(int code, String message) {
        this.message.fail(code, message)
    }

    Object dependency(String key) {
        fiberNode.dependency(key)
    }

    def <T> T dependency(Class<T> type) {
        fiberNode.dependency(type)
    }

}