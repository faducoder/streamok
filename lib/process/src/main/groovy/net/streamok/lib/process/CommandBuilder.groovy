package net.streamok.lib.process

class CommandBuilder {

    private final List<String> command

    File workingDirectory

    boolean sudo = false

    String sudoPassword

    // Constructors

    CommandBuilder(List<String> command) {
        this.command = command
    }

    CommandBuilder(String... command) {
        this.command = command.toList()
    }

    // Factory methods

    static CommandBuilder cmd(String... command) {
        if(command.length == 1 && command[0] =~ /\s+/) {
            cmd(command[0].split(/\s+/))
        } else {
            new CommandBuilder(command.toList())
        }
    }

    static CommandBuilder sudo(String... command) {
        cmd(command).sudo()
    }

    // Build methods

    Command build() {
        new Command(command, workingDirectory, sudo, sudoPassword)
    }

    // Setters

    CommandBuilder workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory
        this
    }

    CommandBuilder sudo(boolean sudo) {
        this.sudo = sudo
        this
    }

    CommandBuilder sudo() {
        this.sudo(true)
        this
    }

    CommandBuilder sudoPassword(String sudoPassword) {
        this.sudoPassword = sudoPassword
        this
    }

}
