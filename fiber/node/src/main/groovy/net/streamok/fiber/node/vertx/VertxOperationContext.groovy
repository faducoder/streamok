package net.streamok.fiber.node.vertx

import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import net.streamok.fiber.node.api.FiberNode
import net.streamok.fiber.node.api.OperationContext
import net.streamok.lib.conf.Conf
import org.slf4j.LoggerFactory

import static io.vertx.core.json.Json.decodeValue
import static org.apache.commons.lang3.Validate.notBlank
import static org.apache.commons.lang3.Validate.notNull

class VertxOperationContext implements OperationContext {

    private final Message message

    private final FiberNode fiberNode

    VertxOperationContext(Message message, FiberNode fiberNode) {
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

    String nonBlankHeader(String name) {
        def value = notNull(header(name), "${name} can't  be null.").toString()
        notBlank(value, "${name} can't be blank.")
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

    def String configurationString(String key, String defaultValue) {
        Conf.configuration().instance().getString(key, defaultValue)
    }

    void debug(String message) {
        LoggerFactory.getLogger("streamok.service.operation").debug(message)
    }

    Vertx vertx() {
        fiberNode.vertx()
    }

}