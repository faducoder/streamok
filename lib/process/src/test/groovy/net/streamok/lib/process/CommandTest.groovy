package net.streamok.lib.process

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static net.streamok.lib.common.Uuids.uuid
import static net.streamok.lib.process.CommandBuilder.cmd
import static net.streamok.lib.process.CommandBuilder.sudo

class CommandTest {

    def command = uuid()

    // Tests

    @Test
    void shouldParseStringBySpace() {
        def command = cmd('foo bar').build()
        assertThat(command.command()).isEqualTo(['foo', 'bar'])
    }

    // Sudo tests

    @Test
    void shouldCreateCommandWithSudoEnabled() {
        def command = sudo(command).build()
        assertThat(command.sudo()).isTrue()
    }

    @Test
    void shouldParseCommandWithSudoEnabled() {
        def command = sudo('foo bar').build()
        assertThat(command.sudo()).isTrue()
        assertThat(command.command()).isEqualTo(['foo', 'bar'])
    }

    @Test
    void shouldCreateCommandWithSudoDisabled() {
        def command = cmd(command).build()
        assertThat(command.sudo()).isFalse()
    }

    // toString() tests

    @Test
    void toStringShouldIncludeWorkingDirectory() {
        def commandToString = cmd(command).workingDirectory(new File('/foo')).build().toString()
        assertThat(commandToString).contains('workingDirectory:/foo')
    }

    @Test
    void toStringShouldIncludeNullWorkingDirectory() {
        def commandToString = cmd(command).build().toString()
        assertThat(commandToString).contains('workingDirectory:null')
    }

}
