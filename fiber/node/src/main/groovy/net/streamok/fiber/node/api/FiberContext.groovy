package net.streamok.fiber.node.api

import io.vertx.core.eventbus.Message

class FiberContext {

    Message message

    FiberContext(Message message) {
        this.message = message
    }

    Object body() {
        message.body()
    }

    void reply(Object payload) {
        message.reply(payload)
    }

}