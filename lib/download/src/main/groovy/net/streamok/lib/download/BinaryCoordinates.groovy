package net.streamok.lib.download

import groovy.transform.ToString

@ToString
class BinaryCoordinates {

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