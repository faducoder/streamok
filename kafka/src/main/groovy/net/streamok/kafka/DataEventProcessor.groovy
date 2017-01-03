package net.streamok.kafka

interface DataEventProcessor {

    void process(DataEvent dataEvent)

}