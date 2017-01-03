package net.streamok.kafka

class DataEvent {

    String type

    Object payload

    String operationType

    String entityId

    String partitionKey

    // Kafka message properties

    long offset

}
