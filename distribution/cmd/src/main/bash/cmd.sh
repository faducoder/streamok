#!/usr/bin/env bash

if [ -z "${STREAMOK_VERSION}" ]; then
  STREAMOK_VERSION=0.0.5
fi

mkdir -p ~/.streamok/downloads

which java > /dev/null 2>&1 || yum install -y java

if [[ $STREAMOK_VERSION != *"SNAPSHOT"* ]]; then
    wget -nc http://search.maven.org/remotecontent?filepath=net/streamok/streamok-distribution-cmd/${STREAMOK_VERSION}/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar\
        -O ~/.streamok/downloads/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar
else
    wget "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=net.streamok&a=streamok-distribution-cmd&v=${STREAMOK_VERSION}&c=fat" \
        -O ~/.streamok/downloads/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar
fi

java -jar ~/.streamok/downloads/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar "$@"