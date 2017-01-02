package net.streamok.adapter.rest

import io.vertx.core.eventbus.EventBus

interface RestBridge {

    public static final String addressHeader = 'streamok_address'

    public static final String restBridgeAddress = 'streamok.rest.bridge'

    void connect(EventBus eventBus)

}