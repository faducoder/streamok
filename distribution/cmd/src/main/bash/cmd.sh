#!/usr/bin/env bash

STREAMOK_VERSION=0.0.4

mkdir -p ~/.streamok/downloads

which java > /dev/null 2>&1 || yum install -y java

wget -nc http://search.maven.org/remotecontent?filepath=net/streamok/streamok-distribution-cmd/${STREAMOK_VERSION}/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar\
 -O ~/.streamok/downloads/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar
java -jar ~/.streamok/downloads/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar start

oc new-app mongo
oc new-app streamok/node:${STREAMOK_VERSION} -e XMX=512m