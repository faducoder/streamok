package net.streamok.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

class DataStreamProducer {

    private final Producer<String, Bytes> kafkaProducer

    DataStreamProducer(Producer<String, Bytes> kafkaProducer) {
        this.kafkaProducer = kafkaProducer
    }

    void send(Event event) {
        def key = event.partitionKey ?: event.entityId.hashCode() % 10 as String
        def payload = new Bytes(new ObjectMapper().writeValueAsBytes(event))
        kafkaProducer.send(new ProducerRecord<String, Bytes>("events.${event.type}", key, payload))
    }

}