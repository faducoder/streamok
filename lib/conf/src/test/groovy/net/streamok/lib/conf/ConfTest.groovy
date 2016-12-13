package net.streamok.lib.conf

import org.junit.Test

import static net.streamok.lib.common.Uuids.uuid
import static net.streamok.lib.conf.Conf.configuration
import static org.assertj.core.api.Assertions.assertThat

class ConfTest {

    def key = uuid()

    // Tests

    @Test
    void shouldReadSystemProperty() {
        System.setProperty(key, 'bar')
        def property = configuration().get().getString(key)
        assertThat(property).isEqualTo('bar')
    }

    @Test
    void shouldReadNullProperty() {
        def property = configuration().get().getString('noSuchValue')
        assertThat(property).isNull()
    }

    @Test
    void inMemoryShouldOverrideSystemSetting() {
        // Given
        System.setProperty('foo', 'system')
        configuration().memory().setProperty('foo', 'memory')

        // When
        def valueRead = configuration().instance().getString('foo')

        assertThat(valueRead).isEqualTo('memory')
    }

    @Test
    void shouldReadEnvUsingSystemPropertyKey() {
        // Given
        System.setProperty('FOO_BAR_BAZ', 'qux')

        // When
        def property = configuration().get().getString('foo.bar.baz')

        // Then
        assertThat(property).isEqualTo('qux')
    }

}