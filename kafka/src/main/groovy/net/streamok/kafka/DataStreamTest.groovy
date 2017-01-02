package net.streamok.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.utils.Bytes

import static net.streamok.kafka.DataStreamProducer.dataStreamProducer

class DataStreamTest {

    public static void main(String[] args) {
        def producer = dataStreamProducer()
        (1..100).each {
            producer.send(new Event(type: 'foo', payload: 'hello!'))
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
        def cons = new KafkaConsumer<>(config)
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