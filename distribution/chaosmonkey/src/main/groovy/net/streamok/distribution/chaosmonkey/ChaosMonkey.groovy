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
        checkConfigurationServiceApiHeartbeat()
        accessConfigurationServiceApi()

        // Document service
        checkDocumentServiceApiHeartbeat()
        accessDocumentServiceApi()
    }

    private void checkConfigurationServiceApiHeartbeat() {
        def latch = new CountDownLatch(1)
        vertx.createHttpClient().getNow(8080, 'localhost', "/metrics/get?key=service.configuration.heartbeat") {
            it.bodyHandler {
                assertThat(it.toString().toLong()).isGreaterThan(0L)
                latch.countDown()
            }
        }
        latch.await(5, SECONDS)
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

    // Document service

    private void checkDocumentServiceApiHeartbeat() {
        def latch = new CountDownLatch(1)
        vertx.createHttpClient().getNow(8080, 'localhost', "/metrics/get?key=service.document.heartbeat") {
            it.bodyHandler {
                assertThat(it.toString().toLong()).isGreaterThan(0L)
                latch.countDown()
            }
        }
        latch.await(5, SECONDS)
    }

    private void accessDocumentServiceApi() {
        def latch = new CountDownLatch(1)
        def collection = randomAlphanumeric(20)
        vertx.createHttpClient().getNow(8080, 'localhost', "/document/count?collection=${collection}") {
            it.bodyHandler {
                assertThat(it.toString().toLong()).isGreaterThanOrEqualTo(0L)
                latch.countDown()
            }
        }
        latch.await(5, SECONDS)
    }

}