package net.streamok.distribution.cmd

import net.streamok.lib.common.Mavens
import net.streamok.lib.download.DownloadManager
import net.streamok.lib.paas.OpenShiftPaas
import net.streamok.lib.process.DefaultProcessManager

import static net.streamok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class StreamokCmd {

    static void main(String... args) {
        def paas = new OpenShiftPaas(new DownloadManager(new DefaultProcessManager(), new File("/tmp/download")), new DefaultProcessManager()).init()
        if(args.first() == 'install' || args.first() == 'start') {
            new DockerInstall().execute()

            println 'Starting OpenShift...'
            paas.start()
            println 'OpenShift started.'
            paas.startService('mongo')
            def streamokVersion = artifactVersionFromDependenciesProperties('net.streamok', 'streamok-lib-common').get()
            paas.startService("streamok/node:${streamokVersion} -e XMX=512m")
        } else if(args.first() == 'reset') {
            paas.reset()
        }
    }

}