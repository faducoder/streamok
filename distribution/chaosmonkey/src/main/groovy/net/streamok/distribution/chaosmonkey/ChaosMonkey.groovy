package net.streamok.distribution.chaosmonkey

import io.vertx.core.Vertx

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import static org.assertj.core.api.Assertions.assertThat

class ChaosMonkey {

    Vertx vertx

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
        String response
        vertx.createHttpClient().getNow(8080, 'localhost', "/configuration/put?key=${key}&value=${value}") {
            vertx.createHttpClient().getNow(8080, 'localhost', "/configuration/get?key=${key}") {
                it.bodyHandler {
                    response = it.toString()
                    latch.countDown()
                }
            }
        }
        latch.await(5, TimeUnit.SECONDS)
        assertThat(response).isEqualTo(value)
    }

}
