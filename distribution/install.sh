#!/usr/bin/env bash

wget -nc https://github.com/openshift/origin/releases/download/v1.4.0-rc1/openshift-origin-server-v1.4.0-rc1.b4e0954-linux-64bit.tar.gz
tar xpf openshift-origin-server-v1.4.0-rc1.b4e0954-linux-64bit.tar.gz
mv openshift-origin-server-v1.4.0-rc1+b4e0954-linux-64bit openshift
nohup openshift/openshift start &
sleep 5
openshift/oc login https://localhost:8443 -u admin -p admin
openshift/oc new-project streamok