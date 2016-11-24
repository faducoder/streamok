package net.streamok.distribution.chaosmonkey

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import io.vertx.core.Vertx
import net.streamok.distribution.node.StreamokNode
import org.junit.Test

class ChaosMonkeyTest {

    @Test
    void shouldSurviveMonkeyRun() {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(27017, Network.localhostIsIPv6()))
                .build();

        def xx = MongodStarter.getDefaultInstance().prepare(mongodConfig).start()

        StreamokNode.main()
        new ChaosMonkey(Vertx.vertx()).run()
    }

}
