#!/usr/bin/env bash

docker build . -t streamok/kafka-broker
docker push streamok/kafka-broker