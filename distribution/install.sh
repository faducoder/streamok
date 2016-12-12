#!/usr/bin/env bash

# Install Docker
which docker > /dev/null || (curl -sSL https://get.docker.com/ | sh && sudo systemctl enable docker.service && sudo systemctl start docker)

wget -nc https://github.com/openshift/origin/releases/download/v1.4.0-rc1/openshift-origin-server-v1.4.0-rc1.b4e0954-linux-64bit.tar.gz
if [ ! -d 'openshift' ]; then
    tar xpf openshift-origin-server-v1.4.0-rc1.b4e0954-linux-64bit.tar.gz
    mv openshift-origin-server-v1.4.0-rc1+b4e0954-linux-64bit openshift
fi

nohup openshift/openshift start &
sleep 10
openshift/oc login https://localhost:8443 -u admin -p admin
openshift/oc new-project streamok

openshift/oc new-app mongo
openshift/oc new-app streamok/node:0.0.4 -e XMX=512m -e SPARK_USER=root