package net.streamok.fiber.node.vertx

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import net.streamok.fiber.node.api.ServicesNode
import net.streamok.fiber.node.api.OperationContext
import org.slf4j.LoggerFactory

import static io.vertx.core.json.Json.decodeValue
import static io.vertx.core.json.Json.encode
import static net.streamok.lib.conf.Conf.configuration
import static net.streamok.lib.vertx.EventBuses.headers
import static org.apache.commons.lang3.Validate.notBlank
import static org.apache.commons.lang3.Validate.notNull

class VertxOperationContext implements OperationContext {

    private final Message message

    private final ServicesNode fiberNode

    VertxOperationContext(Message message, ServicesNode fiberNode) {
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
        configuration().get().getString(key, defaultValue)
    }

    void debug(String message) {
        LoggerFactory.getLogger("streamok.service.operation").debug(message)
    }

    @Override
    def <T> void send(String address, Object body, Map<String, Object> headersMap, Class<T> responseType, Handler<T> responseHandler) {
        fiberNode.vertx().eventBus().send(address, encode(body), headers(headersMap)) {
            if(it.succeeded()) {
                if (Void.class.equals(responseType)) {
                    responseHandler.handle(null)
                } else {
                    def response = decodeValue(it.result().body() as String, responseType)
                    responseHandler.handle(response)
                }
            } else {
                fail(100, it.cause().message)
            }
        }
    }

    @Override
    def void send(String address, Object body, Map<String, Object> headersMap) {
        fiberNode.vertx().eventBus().send(address, encode(body), headers(headersMap)) {
            reply(null)
        }
    }

    Vertx vertx() {
        fiberNode.vertx()
    }

}