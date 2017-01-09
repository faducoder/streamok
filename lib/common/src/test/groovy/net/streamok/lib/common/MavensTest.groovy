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
package net.streamok.lib.common

import org.assertj.core.api.Assertions
import org.junit.Test

import static net.streamok.lib.common.Mavens.MavenCoordinates.parseMavenCoordinates
import static net.streamok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class MavensTest {

    @Test
    void shouldParseCoordinates() {
        // Given
        def group = 'foo'
        def artifact = 'bar'
        def version = '1'
        def coordinatesString = "${group}:${artifact}:${version}"

        // When
        def coordinates = parseMavenCoordinates(coordinatesString)

        // Then
        Assertions.assertThat(coordinates.artifactId).isEqualTo(artifact)
        Assertions.assertThat(coordinates.groupId).isEqualTo(group)
        Assertions.assertThat(coordinates.version).isEqualTo(version)
    }

    @Test
    void shouldParseCoordinatesWithCustomSeparator() {
        // Given
        def group = 'foo'
        def artifact = 'bar'
        def version = '1'
        def coordinatesString = "${group}/${artifact}/${version}"

        // When
        def coordinates = parseMavenCoordinates(coordinatesString, '/')

        // Then
        Assertions.assertThat(coordinates.artifactId).isEqualTo(artifact)
        Assertions.assertThat(coordinates.groupId).isEqualTo(group)
        Assertions.assertThat(coordinates.version).isEqualTo(version)
    }

    @Test
    void shouldLoadDependencyVersion() {
        def version = artifactVersionFromDependenciesProperties('com.test', 'test')
        Assertions.assertThat(version).contains('6.6.6')
    }

    @Test
    void shouldHandleMissingDependency() {
        def version = artifactVersionFromDependenciesProperties('invalidGroupId', 'invalidArtifacts')
        Assertions.assertThat(version).isEmpty()
    }

}
