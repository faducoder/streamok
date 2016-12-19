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
package net.streamok.lib.paas

import net.streamok.lib.download.DownloadManager
import net.streamok.lib.process.DefaultProcessManager
import org.junit.Before
import org.junit.Test

import static OpenShiftPaas.condition
import static com.google.common.io.Files.createTempDir
import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.MINUTES
import static org.assertj.core.api.Assertions.assertThat

class OpenShiftPaasTest {

    // Test subject fixtures

    def processManager = new DefaultProcessManager()

    def paas = new OpenShiftPaas(new DownloadManager(processManager, createTempDir()), processManager).init()

    @Before
    void before() {
        paas.reset()
        paas.start()
    }

    // Tests

    @Test
    void shouldStartAndStop() {
        // Should start
        assertThat(paas.started).isTrue()
        assertThat(paas.provisioned).isTrue()

        // Should start service
        paas.startService('mongo')
        await().atMost(1, MINUTES).until condition {paas.services().find { it.name == 'mongo' } != null}
        def mongoService = paas.services().find { it.name == 'mongo' }
        assertThat(mongoService).isNotNull()

        // Should stop
        paas.stop()
        await().until condition { !paas.started }
    }

    @Test
    void shouldStopAfterReset() {
        // When
        paas.reset()

        // Then
        assertThat(paas.started).isFalse()
    }

}