#!/usr/bin/env bash

docker build . -t streamok/keycloak
docker push streamok/keycloak