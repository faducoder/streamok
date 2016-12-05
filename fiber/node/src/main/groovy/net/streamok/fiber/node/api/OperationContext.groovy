package net.streamok.fiber.node.api

import io.vertx.core.Vertx

interface OperationContext {

    Map body()

    def <T> T body(Class<T> type)

    Object header(String name)

    String nonBlankHeader(String name)

    void reply(Object payload)

    def fail(int code, String message)

    Object dependency(String key)

    def <T> T dependency(Class<T> type)

    def String configurationString(String key, String defaultValue)

    void debug(String message)

    Vertx vertx()

}