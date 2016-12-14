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
package net.smolok.lib.download.spring

import net.smolok.lib.download.DownloadManager
import net.smolok.lib.download.DownloadManager.BinaryCoordinates
import net.smolok.lib.download.FileDownloadException
import net.smolok.lib.download.UnsupportedCompressionFormatException
import net.streamok.lib.process.DefaultProcessManager
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static net.streamok.lib.common.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class DownloadManagerTest {

    // Fixtures

    def downloadDirectory = createTempDir()

    def downloadManager = new DownloadManager(new DefaultProcessManager(), downloadDirectory)

    // Tests

    @Test
    void shouldReturnDownloadDirectory() {
        assertThat(downloadManager.downloadDirectory()).isEqualTo(downloadDirectory)
    }

    @Test
    void shouldDownloadFile() {
        // When
        downloadManager.download(new BinaryCoordinates(
                new URL("http://search.maven.org/remotecontent?filepath=org/wildfly/swarm/guava/1.0.0.Alpha8/guava-1.0.0.Alpha8.jar"),
                'guava.jar'))

        // Then
        def guavaSize = downloadManager.downloadedFile("guava.jar").length()
        assertThat(guavaSize).isGreaterThan(0L)
    }

    @Test
    void shouldDecompressTarGz() {
        // When
        downloadManager.download(new BinaryCoordinates(
                new File("src/test/compressedDirectory.tar.gz").toURI().toURL(),
                "compressedDirectory.tar.gz", "compressedDirectory"));

        // Then
        long uncompressedDirectory = downloadManager.downloadedFile("compressedDirectory").list().length;
        assertThat(uncompressedDirectory).isGreaterThan(0L);
    }

    @Test(expected = UnsupportedCompressionFormatException)
    void shouldHandleUnsupportedCompressionFormat() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new File("src/test/invalidCompressionFormat.xyz").toURI().toURL(),
                "invalidCompressionFormat.xyz", "invalidCompressionFormat"));
    }

    @Test(expected = FileDownloadException)
    void shouldHandleInvalidHost() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new URL("http://smolok-" + uuid() + ".com"), uuid()));
    }

}
