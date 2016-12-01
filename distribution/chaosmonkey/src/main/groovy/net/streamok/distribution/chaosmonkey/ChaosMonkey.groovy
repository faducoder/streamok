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

    ChaosMonkey() {
        this(Vertx.vertx())
    }

    void run() {
        checkConfigurationServiceApiHeartbeat()
        accessConfigurationServiceApi()

        // Document service
        checkDocumentServiceHeartbeat()
        accessDocumentServiceSaveOperation()
        accessDocumentServiceCountOperation()
        checkDocumentServiceCountMetric()
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

    private void checkDocumentServiceHeartbeat() {
        def latch = new CountDownLatch(1)
        vertx.createHttpClient().getNow(8080, 'localhost', "/metrics/get?key=service.document.heartbeat") {
            it.bodyHandler {
                assertThat(it.toString().toLong()).isGreaterThan(0L)
                latch.countDown()
            }
        }
        latch.await(5, SECONDS)
    }

    private void checkDocumentServiceCountMetric() {
        def latch = new CountDownLatch(1)
        vertx.createHttpClient().getNow(8080, 'localhost', "/metrics/get?key=service.document.count") {
            it.bodyHandler {
                assertThat(it.toString().toLong()).isGreaterThan(0L)
                latch.countDown()
            }
        }
        latch.await(30, SECONDS)
    }

    private void accessDocumentServiceSaveOperation() {
        def latch = new CountDownLatch(1)
        def collection = randomAlphanumeric(20)
        vertx.createHttpClient().getNow(8080, 'localhost', "/document/count?collection=${collection}") {
            it.bodyHandler {
                assertThat(it.toString().toLong()).isGreaterThan(0L)
                latch.countDown()
            }
        }
        latch.await(5, SECONDS)
    }

    private void accessDocumentServiceCountOperation() {
        def latch = new CountDownLatch(1)
        def collection = randomAlphanumeric(20)
        vertx.createHttpClient().post(8080, 'localhost', "/document/save?collection=${collection}") {
            assertThat(it.toString()).isNotNull()
            latch.countDown()
        }
        latch.await(5, SECONDS)
    }

}