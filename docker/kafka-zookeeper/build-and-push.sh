#!/usr/bin/env bash

docker build . -t streamok/kafka-zookeeper
docker push streamok/kafka-zookeeper