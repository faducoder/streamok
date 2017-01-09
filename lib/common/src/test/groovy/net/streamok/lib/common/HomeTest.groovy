package net.streamok.lib.common

import org.assertj.core.api.Assertions
import org.junit.Test

import static net.streamok.lib.common.Home.home
import static org.apache.commons.lang3.SystemUtils.userHome

class HomeTest {

    @Test
    void shouldBeLocatedInUserHomeByDefault() {
        def homeRoot = home().root()
        Assertions.assertThat(homeRoot).hasParent(userHome)
    }

}
