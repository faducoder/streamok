package net.streamok.distribution.cmd

import com.google.common.io.Files
import net.streamok.lib.process.DefaultProcessManager
import org.apache.commons.io.IOUtils

import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission

import static java.io.File.createTempFile
import static net.streamok.lib.process.CommandBuilder.cmd

class DockerInstall {

    void execute() {
        def processManager = new DefaultProcessManager()
        try {
            if (processManager.canExecute(cmd('docker info').build())) {
                println 'Docker installed - skipping installation.'
            } else {
                println 'Docker not found. Installing...'
                def installScript = IOUtils.toString(new URL('https://get.docker.com'))
                def scriptFile = createTempFile('streamok', 'tmp')
                Files.write(installScript.bytes, scriptFile)
                java.nio.file.Files.setPosixFilePermissions(Paths.get(scriptFile.absolutePath), [PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ] as Set)
                def out = processManager.execute(cmd("/bin/sh ${scriptFile.absolutePath}").build())
                out = processManager.execute(cmd('systemctl enable docker.service').build())
                out = processManager.execute(cmd('systemctl start docker').build())
                println 'Docker installed.'
            }
        } finally {
            processManager.close()
        }
    }

}
