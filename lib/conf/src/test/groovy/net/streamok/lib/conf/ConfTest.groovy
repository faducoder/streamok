package net.streamok.lib.conf

import org.junit.Test

import static net.streamok.lib.conf.Conf.configuration
import static org.assertj.core.api.Assertions.assertThat

class ConfTest {

    @Test
    void shouldReadSystemProperty() {
        System.setProperty('foo', 'bar')
        def property = configuration().instance().getString('foo')
        assertThat(property).isEqualTo('bar')
    }

    @Test
    void shouldReadNullProperty() {
        def property = configuration().instance().getString('noSuchValue')
        assertThat(property).isNull()
    }

}