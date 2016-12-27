#!/usr/bin/env bash

STREAMOK_VERSION=0.0.4

mkdir -p ~/.streamok/downloads

which javax > /dev/null 2>&1 || yum install -y java

wget -nc http://search.maven.org/remotecontent?filepath=net/streamok/streamok-distribution-cmd/${STREAMOK_VERSION}/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar\
 -O ~/.streamok/downloads/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar
java -jar ~/.streamok/downloads/streamok-distribution-cmd-${STREAMOK_VERSION}-fat.jar install

if [ ! -d 'openshift' ]; then
    wget -nc https://github.com/openshift/origin/releases/download/v1.4.0-rc1/openshift-origin-server-v1.4.0-rc1.b4e0954-linux-64bit.tar.gz
    tar xpf openshift-origin-server-v1.4.0-rc1.b4e0954-linux-64bit.tar.gz
    mv openshift-origin-server-v1.4.0-rc1+b4e0954-linux-64bit openshift
fi

nohup openshift/openshift start &
sleep 10


echo 'export KUBECONFIG=/root/openshift.local.config/master/admin.kubeconfig' >> /root/.bashrc
echo 'export CURL_CA_BUNDLE=/root/openshift.local.config/master/ca.crt' >> /root/.bashrc
echo 'export OC_HOME=/root/openshift' >> /root/.bashrc
echo 'export PATH=$PATH:$OC_HOME' >> /root/.bashrc

. bash
echo '.bashrc reloaded'

chmod +r /root/openshift.local.config/master/admin.kubeconfig

oc login -u system:admin
oc new-project streamok

oadm policy add-scc-to-user anyuid -z default
oadm policy add-role-to-user admin admin -n streamok

oc new-app mongo
oc new-app streamok/node -e XMX=512m

# Configure and create router
oadm policy add-scc-to-user hostnetwork system:serviceaccount:streamok:router
oadm policy add-cluster-role-to-user system:router system:serviceaccount:streamok:router
oadm router streamok-router --service-account=router
oc expose service node --name=node-route --hostname=46.101.209.237
