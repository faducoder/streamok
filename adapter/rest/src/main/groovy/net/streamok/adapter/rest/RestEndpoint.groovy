/**
 * Licensed to the Streamok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.streamok.adapter.rest

import io.vertx.core.eventbus.ReplyException
import io.vertx.core.http.HttpMethod
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.ServicesNode
import net.streamok.lib.vertx.EventBuses

import static net.streamok.adapter.rest.RestBridge.restBridgeAddress
import static net.streamok.lib.conf.Conf.configuration

class RestEndpoint implements Endpoint {

    String prefix = configuration().get().getString('adapter.rest.address.prefix', 'api')

    @Override
    void connect(ServicesNode fiberNode) {
        def vertx = fiberNode.vertx()
        def server = vertx.createHttpServer()
        new EventBusRestBridge().connect(vertx.eventBus())

        server.requestHandler { request ->
            def address = request.path().substring(1).replaceAll('/', '.')
            address = address.replaceFirst("${prefix}.", '')
            def headers = [streamok_address: address]
            headers = request.params().entries().inject(headers) { map, entry -> map[entry.key] = entry.value; map }
            request.bodyHandler {
                vertx.eventBus().send(restBridgeAddress, request.method() == HttpMethod.GET ? null : it.toString(), EventBuses.headers(headers)) {
                    def response = request.response()
                    response.putHeader("content-type", "text/plain")
                    if(it.failed()) {
                        int failureCode = (it.cause() as ReplyException).failureCode() ?: 100
                        if (failureCode != 100) {
                            response.setStatusCode(failureCode)
                        }
                        response.end(it.cause().message)
                    } else {
                        response.end(it.result().body().toString())
                    }
                }
            }
        }

        server.listen(configuration().get().getInt('adapter.rest.port', 8080))
    }

}