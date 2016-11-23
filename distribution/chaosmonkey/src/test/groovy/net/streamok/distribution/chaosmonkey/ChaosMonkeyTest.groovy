package net.streamok.distribution.chaosmonkey

import io.vertx.core.Vertx
import net.streamok.distribution.node.StreamokNode
import org.junit.Test

class ChaosMonkeyTest {

    @Test
    void shouldSurviveMonkeyRun() {
        StreamokNode.main()
        new ChaosMonkey(Vertx.vertx()).run()
    }

}
