package net.streamok.kafka

import org.apache.commons.lang3.RandomStringUtils
import org.apache.kafka.clients.consumer.KafkaConsumer

import static net.streamok.kafka.DataStreamConsumer.dataStreamConsumer
import static net.streamok.kafka.DataStreamProducer.dataStreamProducer

class DataStreamTest {

    public static void main(String[] args) {
        def topic = RandomStringUtils.randomAlphabetic(5)

        def producer = dataStreamProducer()
        (1..5).each {
            producer.send(new DataEvent(type: topic, payload: 'hello!'))
        }
        producer.close()

        def config = new Properties()
        config.put("bootstrap.servers", "localhost:9092")
        config.put("group.id", "testxxxxy");
        config.put("enable.auto.commit", "false");
        config.put("auto.commit.interval.ms", "10");
        config.put("session.timeout.ms", "30000");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put("value.deserializer", "org.apache.kafka.common.serialization.BytesDeserializer")
        config.put("auto.offset.reset", "earliest")
        def cons = new KafkaConsumer<>(config)

        dataStreamConsumer(cons, topic){ println it }.start()
    }

}