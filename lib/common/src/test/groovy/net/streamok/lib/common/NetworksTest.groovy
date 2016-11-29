package net.streamok.lib.common

import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Test

import static java.lang.System.currentTimeMillis
import static java.util.concurrent.TimeUnit.SECONDS
import static org.junit.Assume.assumeTrue

class NetworksTest extends Assert {

    @Test
    void shouldReturnAvailablePort() {
        Assertions.assertThat(Networks.findAvailableTcpPort()).isGreaterThan(Networks.MIN_PORT_NUMBER)
    }

    @Test
    void shouldReachHost() {
        assumeTrue('This test should be executed only if you can access rhiot.io from your network.',
                Networks.isReachable('rhiot.io', (int) SECONDS.toMillis(10)))
    }

    @Test
    void shouldNotReachHost() {
        Assertions.assertThat(Networks.isReachable("someUnreachableHostName${currentTimeMillis()}")).isFalse()
    }

    @Test
    void shouldReturnCurrentLocalNetworkIp() {
        Assertions.assertThat(Networks.currentLocalNetworkIp()).isNotNull()
    }

}
