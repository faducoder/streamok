package net.streamok.lib.vertx

import io.vertx.core.Handler
import org.apache.commons.lang3.Validate

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS

class Blocking {

    static def block(Handler<CountDownLatch> handler) {
        def sempahore = new CountDownLatch(0)
        handler.handle(sempahore)
        Validate.isTrue(sempahore.await(15, SECONDS))
    }

}
