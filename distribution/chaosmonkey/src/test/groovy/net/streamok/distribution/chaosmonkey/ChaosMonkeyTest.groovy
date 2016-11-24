package net.streamok.distribution.chaosmonkey

import io.vertx.core.Vertx
import net.streamok.distribution.node.StreamokNode
import net.streamok.lib.mongo.EmbeddedMongo
import org.junit.Test

class ChaosMonkeyTest {

    @Test
    void shouldSurviveMonkeyRun() {
        new EmbeddedMongo().start()
        StreamokNode.main()
        new ChaosMonkey(Vertx.vertx()).run()
    }

}
