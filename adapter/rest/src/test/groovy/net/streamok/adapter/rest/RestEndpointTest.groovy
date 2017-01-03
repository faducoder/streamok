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

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.streamok.fiber.node.DefaultServicesNode
import net.streamok.fiber.node.FiberDefinitionFactory
import net.streamok.fiber.node.api.ServicesNode
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static net.streamok.lib.common.Networks.findAvailableTcpPort
import static net.streamok.lib.conf.Conf.configuration

@RunWith(VertxUnitRunner)
class RestEndpointTest {

    int port = findAvailableTcpPort()

    ServicesNode servicesNode

    @Before
    void before() {
        configuration().memory().setProperty('adapter.rest.port', port)
        servicesNode = new DefaultServicesNode().addEndpoint(new RestEndpoint())
    }

    @After
    void after() {
        servicesNode.close()
    }

    @Test
    void shouldInvokeOperationViaRestEndpoint(TestContext context) {
        def async = context.async()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.body())}']
        servicesNode.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        servicesNode.vertx().createHttpClient().getNow(port, 'localhost', '/echo') {
            it.bodyHandler {
                context.assertEquals(it.toString(), 'null')
                async.complete()
            }
        }
    }

    @Test
    void shouldCopyParameterToHeader(TestContext context) {
        def async = context.async()
        def fiberDefinition = [type: 'groovy', address: 'echo', closure: '{it -> it.reply(it.nonBlankHeader("foo"))}']
        servicesNode.addFiber(new FiberDefinitionFactory().build(fiberDefinition))
        servicesNode.vertx().createHttpClient().getNow(port, 'localhost', '/echo?foo=bar') {
            it.bodyHandler {
                context.assertEquals(it.toString(), 'bar')
                async.complete()
            }
        }
    }

}