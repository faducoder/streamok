#!/usr/bin/env bash

echo XXXXXXXXXXXXX
echo $KAFKA_BROKER_SERVICE_HOST
echo XXXXXXXXXXXXX
/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties --override zookeeper.connect="${KAFKA_ZOOKEEPER_SERVICE_HOST}:${KAFKA_ZOOKEEPER_SERVICE_PORT}" --override advertised.host.name="${KAFKA_BROKER_SERVICE_HOST}"