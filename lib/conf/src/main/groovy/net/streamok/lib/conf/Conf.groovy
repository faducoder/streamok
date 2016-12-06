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
package net.streamok.lib.conf

import org.apache.commons.configuration2.*

class Conf {

    private static final Conf conf = new Conf()

    private final CompositeConfiguration configuration

    // Constructors

    Conf() {
        configuration = new CompositeConfiguration()
        configuration.addConfiguration(new MapConfiguration([:]), true)
        configuration.addConfiguration(new SystemConfiguration())
        configuration.addConfiguration(new EnvironmentConfiguration())
    }

    static configuration() {
        conf
    }

    Configuration get() {
        configuration
    }

    @Deprecated
    Configuration instance() {
        configuration
    }

    Configuration memory() {
        configuration.inMemoryConfiguration
    }

}