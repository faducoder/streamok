package net.streamok.distribution.cmd

import net.streamok.lib.download.DownloadManager
import net.streamok.lib.paas.OpenShiftPaas
import net.streamok.lib.process.DefaultProcessManager

class StreamokCmd {

    static void main(String... args) {
        if(args.first() == 'install' || args.first() == 'start') {
            new DockerInstall().execute()
            println 'Starting OpenShift...'
            new OpenShiftPaas(new DownloadManager(new DefaultProcessManager(), new File("/tmp/download")), new DefaultProcessManager()).init().start()
        }
    }

}