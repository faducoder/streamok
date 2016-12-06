package net.streamok.lib.process

import org.assertj.core.api.Assertions
import org.junit.Test

import static net.streamok.lib.conf.Conf.configuration
import static net.streamok.lib.process.SudoResolver.resolveSudo

class SudoResolverTest {

    @Test
    void nonRootWithNonEmptyPasswordShouldUseSudoInPipe() {
        // Given
        configuration().memory().setProperty('user.name', 'notRoot')
        def command = CommandBuilder.sudo('echo foo').sudoPassword('nonEmptyPassword').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.last()).contains('sudo')
    }

    @Test
    void nonRootWithBlankPasswordShouldUseSudoInPipe() {
        // Given
        configuration().memory().setProperty('user.name', 'notRoot')
        def command = CommandBuilder.sudo('echo foo').sudoPassword(' ').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.last()).contains('sudo')
    }

    @Test
    void nonRootWithEmptyPasswordShouldUseSudoPrefix() {
        // Given
        configuration().memory().setProperty('user.name', 'notRoot')
        def command = CommandBuilder.sudo('echo foo').sudoPassword('').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand.first()).isEqualTo('sudo')
    }

    @Test
    void rootShouldNotUseSudo() {
        // Given
        configuration().memory().setProperty('user.name', 'root')
        def command = CommandBuilder.sudo('echo foo').build()

        // When
        def enhancedCommand = resolveSudo(command)

        // Then
        Assertions.assertThat(enhancedCommand).isEqualTo(['echo', 'foo'])
    }

}
