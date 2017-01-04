package net.streamok.kafka

import org.apache.commons.lang3.RandomStringUtils

import static net.streamok.kafka.DataStreamConsumer.dataStreamConsumer
import static net.streamok.kafka.DataStreamProducer.dataStreamProducer

class DataStreamTest {

    public static void main(String[] args) {
        def topic = RandomStringUtils.randomAlphabetic(5)

        def producer = dataStreamProducer()
        (1..5).each {
            producer.send(topic, new DataEvent(payload: 'hello!'))
        }
        producer.close()

        dataStreamConsumer(topic){ println it }.start()
    }

}