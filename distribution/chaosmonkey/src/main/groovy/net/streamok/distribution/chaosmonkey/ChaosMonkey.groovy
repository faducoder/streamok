package net.streamok.distribution.chaosmonkey

import io.vertx.core.Vertx

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import static org.assertj.core.api.Assertions.assertThat

class ChaosMonkey {

    private final Vertx vertx

    ChaosMonkey(Vertx vertx) {
        this.vertx = vertx
    }

    void run() {
        accessConfigurationServiceApi()
    }

    private void accessConfigurationServiceApi() {
        def latch = new CountDownLatch(1)
        def key = randomAlphanumeric(20)
        def value = randomAlphanumeric(20)
        vertx.createHttpClient().getNow(8080, 'localhost', "/configuration/write?key=${key}&value=${value}") {
            vertx.createHttpClient().getNow(8080, 'localhost', "/configuration/read?key=${key}") {
                it.bodyHandler {
                    assertThat(it.toString()).isEqualTo(value)
                    latch.countDown()
                }
            }
        }
        latch.await(5, SECONDS)
    }

}
