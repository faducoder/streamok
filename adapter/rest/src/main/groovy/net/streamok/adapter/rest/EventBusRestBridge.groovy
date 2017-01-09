package net.streamok.adapter.rest

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.ReplyException

class EventBusRestBridge implements RestBridge {

    @Override
    void connect(EventBus eventBus) {
        eventBus.consumer(restBridgeAddress) { message ->
            def address = message.headers().get(addressHeader)
            eventBus.send(address, message.body(), new DeliveryOptions().setHeaders(message.headers())) {
                if(it.succeeded()) {
                    message.reply(it.result().body())
                } else {
                    int failureCode = (it.cause() as ReplyException).failureCode() ?: 100
                    message.fail(failureCode, it.cause().message)
                }
            }
        }
    }

}