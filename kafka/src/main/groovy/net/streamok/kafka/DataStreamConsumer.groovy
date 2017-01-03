package net.streamok.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.utils.Bytes

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
