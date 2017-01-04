package net.streamok.kafka

class DataEvent {

    Object payload

    String operationType

    String entityId

    String partitionKey

    // Kafka message properties

    long offset

}
