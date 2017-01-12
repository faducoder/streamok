package net.streamok.distribution.cmd

import net.streamok.lib.common.Closeable
import net.streamok.lib.common.Initable
import net.streamok.lib.download.DownloadManager
import net.streamok.lib.paas.OpenShiftPaas
import net.streamok.lib.process.DefaultProcessManager

import static net.streamok.lib.common.Home.home
import static net.streamok.lib.common.Mavens.artifactVersionFromDependenciesProperties

class StreamokCmd {

    static void main(String... args) {
        def services = []

        def streamokHome = home()

        def processManager = new DefaultProcessManager()
        services << processManager

        def paas = new OpenShiftPaas(new DownloadManager(processManager, new File(streamokHome.root(), 'downloads')), processManager)
        services << paas

        services.each { if(it instanceof Initable) it.init() }

        if(args.first() == 'install' || args.first() == 'start') {
            new DockerInstall().execute()

            println 'Starting OpenShift...'
            paas.start()
            println 'OpenShift started.'
            paas.startService('mongo')
            paas.startService('streamok/keycloak')
            paas.startService('streamok/kafka-zookeeper')
            paas.startService('streamok/kafka-broker')
            def streamokVersion = artifactVersionFromDependenciesProperties('net.streamok', 'streamok-lib-common').get()
            paas.startService("streamok/node:${streamokVersion} -e XMX=512m")
        } else if(args.first() == 'reset') {
            println 'Resetting OpenShift installation...'
            paas.reset()
            println 'Done.'
        }

        services.each { if(it instanceof Closeable) it.close() }
    }

}