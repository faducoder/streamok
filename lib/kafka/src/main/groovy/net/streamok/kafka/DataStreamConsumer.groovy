/**
 * Licensed to the Streamok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.streamok.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.utils.Bytes

import static net.streamok.lib.conf.Conf.configuration

class DataStreamConsumer {

    private final Consumer<String, Bytes> consumer

    private final DataEventProcessor eventProcessor

    private final String eventType

    DataStreamConsumer(Consumer<String, Bytes> consumer, DataEventProcessor eventProcessor, String eventType) {
        this.consumer = consumer
        this.eventProcessor = eventProcessor
        this.eventType = eventType
    }

    static DataStreamConsumer dataStreamConsumer(Consumer<String, Bytes> consumer, String eventType, DataEventProcessor eventProcessor) {
        new DataStreamConsumer(consumer, eventProcessor, eventType)
    }

    static DataStreamConsumer dataStreamConsumer(String eventType, DataEventProcessor eventProcessor) {
        def config = new Properties()
        config.put('bootstrap.servers', configuration().get().getString('datastream.bootstrap.servers', 'localhost:9092'))
        config.put('group.id', 'consumerGroup')
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put("value.deserializer", "org.apache.kafka.common.serialization.BytesDeserializer")
        config.put("enable.auto.commit", "false")
        config.put("auto.offset.reset", "earliest")
        def consumer = new KafkaConsumer<>(config)
        new DataStreamConsumer(consumer, eventProcessor, eventType)
    }

    void start() {
        consumer.subscribe(["events.${eventType}".toString()])
        while (true) {
            def events = consumer.poll(5000).iterator()
            while (events.hasNext()) {
                def record = events.next()
                def event = new ObjectMapper().readValue(record.value().get(), DataEvent)
                event.offset = record.offset()
                eventProcessor.process(event)
                consumer.commitSync()
            }
            Thread.sleep(100)
        }
    }

}
