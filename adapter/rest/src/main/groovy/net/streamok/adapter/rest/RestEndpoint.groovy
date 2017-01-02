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

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.FiberNode

class RestEndpoint implements Endpoint {

    @Override
    void connect(FiberNode fiberNode) {
        def vertx = fiberNode.vertx()
        def server = vertx.createHttpServer()

        server.requestHandler { request ->
            def address = request.uri().substring(1).replaceAll('/', '.')
            if(address.indexOf('?') != -1) {
                address = address.substring(0, address.indexOf('?'))
            }
            def dd = new DeliveryOptions()
            request.params().entries().each { dd.addHeader(it.key, it.value) }
            request.bodyHandler {
                vertx.eventBus().send(address, request.method() == HttpMethod.GET ? null : it.toString(), dd) {
                    HttpServerResponse response = request.response()
                    response.putHeader("content-type", "text/plain")
                    if(it.failed()) {
                        response.end(it.cause().message)
                    } else {
                        response.end(it.result().body().toString())
                    }
                }
            }
        }

        server.listen(8080)
    }

}