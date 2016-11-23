package net.streamok.fiber.node

import io.vertx.core.Handler
import io.vertx.core.eventbus.Message

interface FiberDefinition {

    String address()

    Handler<Message> handler()

}