package net.streamok.distribution.chaosmonkey

import io.vertx.core.Vertx

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static org.assertj.core.api.Assertions.assertThat

class ChaosMonkey {

    Vertx vertx

    ChaosMonkey(Vertx vertx) {
        this.vertx = vertx
    }

    void run() {
        def latch = new CountDownLatch(1)
        String response
        vertx.createHttpClient().getNow(8080, 'localhost', '/configuration/put?key=foo&value=bar') {
            vertx.createHttpClient().getNow(8080, 'localhost', '/configuration/get?key=foo') {
                it.bodyHandler {
                    response = it.toString()
                    latch.countDown()
                }
            }
        }
        latch.await(5, TimeUnit.SECONDS)
        assertThat(response).isEqualTo('bar')
    }

}
