package net.streamok.lib.common

import groovy.transform.Immutable
import org.apache.commons.lang3.SystemUtils

import java.nio.file.Paths
import java.util.Properties as JProperties

import static java.lang.String.format
import static org.slf4j.LoggerFactory.getLogger

final class Mavens {

    private static final DEPENDENCIES_PROPERTIES_PATH = "META-INF/maven/dependencies.properties"

    // Static collaborators

    private static final VERSIONS = new JProperties()

    private static final LOG = getLogger(Mavens.class)

    // Static initializer

    static {
        Mavens.class.getClassLoader().getResources(DEPENDENCIES_PROPERTIES_PATH).toSet().each {
            LOG.debug('Loading properties file{}', it)
            VERSIONS.load(it.openStream())
        }
    }

    // Constructors

    private Mavens() {
    }

    /**
     * Returns local Maven repository.
     *
     * @return {@code java.io.File} pointing to the local Maven repository.
     */
    static File localMavenRepository() {
        Paths.get(SystemUtils.USER_HOME, '.m2', 'repository').toFile()
    }

    static Optional<String> artifactVersionFromDependenciesProperties(String groupId, String artifactId) {
        Optional.ofNullable(VERSIONS.getProperty(format("%s/%s/version", groupId, artifactId)))
    }

    // Static classes

    @Immutable
    static class MavenCoordinates {

        public static final def DEFAULT_COORDINATES_SEPARATOR = ':'

        String groupId

        String artifactId

        String version

        static MavenCoordinates parseMavenCoordinates(String coordinates) {
            parseMavenCoordinates(coordinates, DEFAULT_COORDINATES_SEPARATOR)
        }

        static MavenCoordinates parseMavenCoordinates(String coordinates, String separator) {
            def parsedCoordinates = coordinates.split(separator)
            new MavenCoordinates(parsedCoordinates[0], parsedCoordinates[1], parsedCoordinates[2]);
        }

    }

}