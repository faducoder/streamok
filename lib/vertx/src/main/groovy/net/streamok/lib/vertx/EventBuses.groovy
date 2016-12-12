package net.streamok.lib.vertx

import io.vertx.core.eventbus.DeliveryOptions

final class EventBuses {

    private EventBuses() {
    }

    static DeliveryOptions headers(Map<String, Object> headers) {
        def options = new DeliveryOptions()
        headers.entrySet().each {
            options.addHeader(it.key, it.value.toString())
        }
        options
    }

}
