package net.streamok.lib.process

import org.junit.Test

import static net.streamok.lib.process.Command.cmd
import static org.assertj.core.api.Assertions.assertThat

class EchoMockProcessManagerTest {

    def processManager = new EchoMockProcessManager()

    // Tests

    @Test
    void shouldReturnEcho() {
        def output = processManager.execute(cmd('foo'))
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldReturnEchoAsynchronously() {
        def output = processManager.executeAsync(cmd('foo')).get()
        assertThat(output).isEqualTo(['foo'])
    }

}
