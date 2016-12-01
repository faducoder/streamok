package net.streamok.distribution.cmd

class StreamokCmd {

    static void main(String... args) {
        if(args.first() == 'version') {
            println '0.0.1'
        }
    }

}
