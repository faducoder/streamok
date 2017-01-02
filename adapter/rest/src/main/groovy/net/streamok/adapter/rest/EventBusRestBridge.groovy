package net.streamok.adapter.rest

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus

class EventBusRestBridge implements RestBridge {

    @Override
    void connect(EventBus eventBus) {
        eventBus.consumer(restBridgeAddress) { message ->
            def address = message.headers().get(addressHeader)
            eventBus.send(address, message.body(), new DeliveryOptions().setHeaders(message.headers())) {
                if(it.succeeded()) {
                    message.reply(it.result().body())
                } else {
                    message.fail(100, it.cause().message)
                }
            }
        }
    }

}