package net.streamok.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import net.streamok.lib.common.Uuids
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

import static net.streamok.lib.common.Uuids.uuid

class DataStreamProducer {

    private final Producer<String, Bytes> kafkaProducer

    DataStreamProducer(Producer<String, Bytes> kafkaProducer) {
        this.kafkaProducer = kafkaProducer
    }

    static DataStreamProducer dataStreamProducer() {
        def config = new Properties()
        config.put("acks", "all");
        config.put("retries", 0);
        config.put("linger.ms", 1);
        config.put("bootstrap.servers", "localhost:9092")
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        config.put("value.serializer", "org.apache.kafka.common.serialization.BytesSerializer")
        def kafkaProducer = new KafkaProducer(config)
        new DataStreamProducer(kafkaProducer)
    }

    void send(Event event) {
        if(event.entityId == null) {
            event.entityId = uuid()
        }

        def key = event.partitionKey ?: event.entityId.hashCode() % 10 as String
        def payload = new Bytes(new ObjectMapper().writeValueAsBytes(event))
        kafkaProducer.send(new ProducerRecord<String, Bytes>("events.${event.type}", key, payload))
    }

    def close() {
        kafkaProducer.close()
    }
}