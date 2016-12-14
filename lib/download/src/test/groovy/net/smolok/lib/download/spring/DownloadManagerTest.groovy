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

public class DownloadManagerTest {

    def downloadManager = new DownloadManager(new DefaultProcessManager(), createTempDir())

    // Tests

    @Test
    void shouldDownloadFile() {
        // When
        downloadManager.download(new BinaryCoordinates(
                new URL("http://search.maven.org/remotecontent?filepath=org/wildfly/swarm/guava/1.0.0.Alpha8/guava-1.0.0.Alpha8.jar"),
                "guava.jar"));

        // Then
        long guavaSize = downloadManager.downloadedFile("guava.jar").length();
        assertThat(guavaSize).isGreaterThan(0L);
    }

    @Test
    void shouldDecompressTarGz() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(
                new File("src/test/compressedDirectory.tar.gz").toURI().toURL(),
                "compressedDirectory.tar.gz", "compressedDirectory"));

        // Then
        long uncompressedDirectory = downloadManager.downloadedFile("compressedDirectory").list().length;
        assertThat(uncompressedDirectory).isGreaterThan(0L);
    }

    @Test(expected = UnsupportedCompressionFormatException.class)
    void shouldHandleUnsupportedCompressionFormat() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new File("src/test/invalidCompressionFormat.xyz").toURI().toURL(),
                "invalidCompressionFormat.xyz", "invalidCompressionFormat"));
    }

    @Test(expected = FileDownloadException.class)
    void shouldHandleInvalidHost() throws MalformedURLException {
        // When
        downloadManager.download(new BinaryCoordinates(new URL("http://smolok-" + uuid() + ".com"), uuid()));
    }

}
