package net.streamok.fiber.node.api

import io.vertx.core.eventbus.Message
import net.streamok.fiber.node.DefaultFiberNode

class FiberContext {

    private final Message message

    private final FiberNode fiberNode

    FiberContext(Message message, FiberNode fiberNode) {
        this.message = message
        this.fiberNode = fiberNode
    }

    Object body() {
        message.body()
    }

    Object header(String name) {
        message.headers().get(name)
    }

    void reply(Object payload) {
        message.reply(payload)
    }

    Object dependency(String key) {
        fiberNode.dependency(key)
    }

    def <T> T dependency(Class<T> type) {
        fiberNode.dependency(type)
    }

}