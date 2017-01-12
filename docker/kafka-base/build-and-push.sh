#!/usr/bin/env bash

docker build . -t streamok/kafka-base
docker push streamok/kafka-base