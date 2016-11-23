package net.streamok.fiber.node.api

import io.vertx.core.eventbus.Message
import net.streamok.fiber.node.FiberNode

class FiberContext {

    Message message

    FiberNode fiberNode

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
        fiberNode.dependencies[key]
    }

}