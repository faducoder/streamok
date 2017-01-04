package net.streamok.adapter.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.eventbus.EventBus
import net.streamok.kafka.DataEvent
import net.streamok.kafka.DataStreamProducer

class KafkaRestBridge implements RestBridge {

    private final DataStreamProducer dataStreamProducer

    KafkaRestBridge(DataStreamProducer dataStreamProducer) {
        this.dataStreamProducer = dataStreamProducer
    }

    @Override
    void connect(EventBus eventBus) {
        eventBus.consumer(restBridgeAddress) { message ->
            def address = message.headers().get(addressHeader)
            if(!address.startsWith('events.')) {
                message.fail(100, 'Kafka rest bridge address must start with "events." prefix.')
            }
            def event = new ObjectMapper().readValue(message.body() as String, DataEvent)
            dataStreamProducer.send(address.replaceFirst(/events\./, ''), event)
            message.reply('OK')
        }
    }

}