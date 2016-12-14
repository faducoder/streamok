/**
 * Licensed to the Streamok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.smolok.lib.download

import groovy.transform.ToString
import net.streamok.lib.process.ProcessManager
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.Validate

import java.nio.file.Paths
import java.util.zip.ZipInputStream

import static net.streamok.lib.process.Command.cmd
import static org.apache.commons.io.IOUtils.copyLarge
import static org.slf4j.LoggerFactory.getLogger

/**
 * Downloads and caches binary files.
 */
class DownloadManager {

    // Logger

    private final static LOG = getLogger(DownloadManager.class)

    // Collaborators

    private final ProcessManager processManager

    // Configuration members

    private final File downloadDirectory

    // Constructors

    DownloadManager(ProcessManager processManager, File downloadDirectory) {
        this.processManager = processManager
        this.downloadDirectory = downloadDirectory

        downloadDirectory.mkdirs()
    }

    // Download operations

    void download(BinaryCoordinates coordinates) {
        Validate.notNull(coordinates.source(), 'Source URL cannot be null.')
        Validate.notNull(coordinates.fileName(), 'Please indicate the name of the target file.')
        LOG.debug('About to download file using the following coordinates: {}', coordinates)

        def targetFile = downloadedFile(coordinates.fileName)
        if(!targetFile.exists()) {
            LOG.debug('File {} does not exist - downloading...', targetFile.absolutePath)
            def tmpFile = File.createTempFile('smolok', 'download')
            try {
                copyLarge(coordinates.source().openStream(), new FileOutputStream(tmpFile))
            } catch (UnknownHostException e) {
                throw new FileDownloadException(targetFile.name, e)
            }
            targetFile.parentFile.mkdirs()
            tmpFile.renameTo(targetFile)
            LOG.debug('Saved downloaded file to {}.', targetFile.absolutePath)
        } else {
            LOG.debug('File {} exists - download skipped.', targetFile)
        }

        if(coordinates.extractedFileName != null) {
            def extractedImage = downloadedFile(coordinates.extractedFileName)
            if (!extractedImage.exists()) {
                if(targetFile.name.endsWith('.zip')) {
                    def zip = new ZipInputStream(new FileInputStream(targetFile))
                    zip.nextEntry
                    IOUtils.copyLarge(zip, new FileOutputStream(extractedImage))
                    zip.close()
                } else if(targetFile.name.endsWith('.tar.gz')) {
                    extractedImage.mkdirs()
                    processManager.execute(cmd("tar xvpf ${targetFile} -C ${extractedImage}"))
                } else {
                    throw new UnsupportedCompressionFormatException(targetFile.name)
                }
            }
        }
    }

    // File access operations

    File downloadDirectory() {
        downloadDirectory
    }

    File downloadedFile(String name) {
        Validate.notBlank(name, 'Name of the downloaded file cannot be blank.')
        new File(downloadDirectory, name)
    }

    File fileFromExtractedDirectory(String extractedDirectoryName, String filename) {
        Paths.get(downloadedFile(extractedDirectoryName).absolutePath, filename).toFile()
    }

    // Inner classes

    @ToString
    static class BinaryCoordinates {

        private final URL source

        private final String fileName

        private final String extractedFileName

        BinaryCoordinates(URL source, String fileName, String extractedFileName) {
            this.source = source
            this.fileName = fileName
            this.extractedFileName = extractedFileName
        }

        BinaryCoordinates(URL source, String fileName) {
            this(source, fileName, null)
        }


        URL source() {
            source
        }

        String fileName() {
            fileName
        }

        String extractedFileName() {
            extractedFileName
        }

    }

}