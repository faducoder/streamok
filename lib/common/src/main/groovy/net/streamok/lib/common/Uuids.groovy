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
package net.streamok.lib.common

import groovy.transform.CompileStatic
import org.slf4j.Logger

import static java.util.UUID.randomUUID
import static org.slf4j.LoggerFactory.getLogger

/**
 * Utilities for working with the UUIDs.
 */
@CompileStatic
final class Uuids {

    // Logger

    private static final Logger LOG = getLogger(Uuids)

    // Constructors

    private Uuids() {
    }

    // Operations

    /**
     * Generates random UUID string.
     *
     * @return new random UUID string.
     */
    static String uuid() {
        def result = randomUUID().toString()
        LOG.debug('Generated UUID: {}', result)
        result
    }

}
