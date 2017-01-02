package net.streamok.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.utils.Bytes

class DataStreamTest {

    public static void main(String[] args) {
        def props2 = new Properties()
        props2.put("acks", "all");
        props2.put("retries", 0);
        props2.put("batch.size", 16384);
        props2.put("linger.ms", 1);
        props2.put("buffer.memory", 33554432);
        props2.put("bootstrap.servers", "localhost:9092")
        props2.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props2.put("value.serializer", "org.apache.kafka.common.serialization.BytesSerializer")

        def prod = new KafkaProducer(props2)
        def producer = new DataStreamProducer(prod)
        (1..100).each {
            producer.send(new Event(type: 'foo', payload: 'hello!', entityId: 'xxx'))
        }
        prod.close()

        Properties consProp = new Properties()
        consProp.put("bootstrap.servers", "localhost:9092")
        consProp.put("group.id", "testxxxxy");
        consProp.put("enable.auto.commit", "false");
        consProp.put("auto.commit.interval.ms", "10");
        consProp.put("session.timeout.ms", "30000");
        consProp.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        consProp.put("value.deserializer", "org.apache.kafka.common.serialization.BytesDeserializer")
        def cons = new KafkaConsumer<>(consProp)
//        cons.subscribe(['page_visitsx'])
        cons.assign([new TopicPartition('events.foo', 0)])
        cons.seekToBeginning([new TopicPartition('events.foo', 0)])
        ConsumerRecords<String, Bytes> records = cons.poll(10000);
        println records.size()
//            for (ConsumerRecord<String, String> record : records)
        cons.commitSync()
        def first = records.toList().first()
        def last = records.toList().last()
        println new ObjectMapper().readValue(first.value().get(), Map)
        println new ObjectMapper().readValue(last.value().get(), Map)
    }

}