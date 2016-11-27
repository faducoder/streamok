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
package net.streamok.fiber.node

import io.vertx.core.Vertx
import net.streamok.fiber.node.api.*

import static org.apache.commons.lang3.SystemUtils.javaIoTmpDir

class DefaultFiberNode implements FiberNode {

    private final def Vertx vertx

    def dependencies = [:]

    DefaultFiberNode() {
        System.setProperty('vertx.cacheDirBase', javaIoTmpDir.absolutePath)
        vertx = Vertx.vertx()
    }

    DefaultFiberNode addFiber(FiberDefinition fiberDefinition) {
        vertx.eventBus().consumer(fiberDefinition.address()){fiberDefinition.handler().handle(new FiberContext(it, this))}
        this
    }

    DefaultFiberNode addEndpoint(Endpoint endpoint) {
        endpoint.connect(this)
        this
    }

    DefaultFiberNode addSuite(FiberSuite fiberSuite) {
        if(fiberSuite instanceof FiberNodeAware) {
            fiberSuite.fiberNode(this)
        }
        fiberSuite.dependencyProviders().each { addDependency(it) }
        fiberSuite.fiberDefinitions().each { addFiber(it) }
        this
    }

    // Dependency injection

    DefaultFiberNode addDependency(DependencyProvider dependencyProvider) {
        dependencies[dependencyProvider.key()] = dependencyProvider.dependency()
        this
    }

    Object dependency(String key) {
        dependencies[key]
    }

    def <T> T dependency(Class<T> type) {
        dependencies.values().find { type.isAssignableFrom(it.getClass()) }
    }

    // Getters

    Vertx vertx() {
        vertx
    }

}