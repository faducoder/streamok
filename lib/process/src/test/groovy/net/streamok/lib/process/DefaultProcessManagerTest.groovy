package net.streamok.lib.process

import org.junit.Test

import java.util.concurrent.ExecutionException

import static java.io.File.createTempFile
import static net.streamok.lib.process.Command.cmd
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.fail

class DefaultProcessManagerTest {

    // Collaborators fixtures

    def processManager = new DefaultProcessManager()

    // Tests

    @Test
    void shouldBeAbleToExecuteEcho() {
        def canExecuteEcho = processManager.canExecute(cmd('echo'))
        assertThat(canExecuteEcho).isTrue()
    }

    @Test
    void shouldNotBeAbleToExecuteRandomCommand() {
        def canExecuteEcho = processManager.canExecute(cmd('invalidCommand'))
        assertThat(canExecuteEcho).isFalse()
    }

    @Test
    void shouldRunEcho() {
        def output = processManager.execute(cmd('echo', 'foo'))
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldHandleInvalidCommand() {
        try {
            processManager.execute(cmd('invalidCommand'))
        } catch (ProcessExecutionException e) {
            assertThat(e).hasCauseInstanceOf(IOException.class)
            return
        }
        fail('Expected process exception')
    }

    @Test
    void shouldRunEchoAsynchronously() {
        def output = processManager.executeAsync(cmd('echo', 'foo'))
        assertThat(output.get()).isEqualTo(['foo'])
    }

    @Test
    void shouldHandleInvalidAsynchronousCommand() {
        try {
            processManager.executeAsync(cmd('invalidCommand')).get()
        } catch (ExecutionException e) {
            assertThat(e).hasCauseInstanceOf(ProcessExecutionException.class)
            return
        }
        fail('Expected process exception')
    }

    @Test
    void shouldParseCommandWithSpaces() {
        def output = processManager.execute(cmd('echo foo'))
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldParseCommandWithDoubleSpaces() {
        def output = processManager.execute(cmd('echo  foo'))
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldParseCommandWithNewLines() {
        def output = processManager.execute(cmd('echo\nfoo'))
        assertThat(output).isEqualTo(['foo'])
    }

    @Test
    void shouldChangeWorkingDirectory() {
        // Given
        def tempFile = createTempFile('smolok', 'test')
        def command = new CommandBuilder('ls').workingDirectory(tempFile.parentFile).build()

        // When
        def output = processManager.execute(command)

        // Then
        assertThat(output).contains(tempFile.name)
    }

}
