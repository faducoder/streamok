/**
 * Licensed to the Smolok under one or more
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
package net.smolok.paas

/**
 * Point of contact with PaaS implementation capable of starting and managing containers.
 */
interface Paas {

    boolean isProvisioned()

    boolean isStarted()

    /**
     * Starts PaaS platform and event bus on the top of it. Before the method call ends, both PaaS and event bus must be
     * up and running.
     *
     * Nothing happens if this method is called while platform bus is started already.
     */
    void start()

    void stop()

    void reset()

    List<ServiceEndpoint> services()

    void startService(String serviceLocator)

}