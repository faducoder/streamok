package net.streamok.distribution.cmd

class StreamokCmd {

    static void main(String... args) {
        if(args.first() == 'install') {
            new DockerInstall().execute()
        }
    }

}
