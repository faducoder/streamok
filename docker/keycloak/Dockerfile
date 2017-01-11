FROM jboss/keycloak-mongo

MAINTAINER hekonsek@gmail.com

USER root
RUN chgrp -R 0 /opt/jboss/keycloak
RUN chmod -R g+rw /opt/jboss/keycloak
RUN find /opt/jboss/keycloak -type d -exec chmod g+x {} +
RUN rm -rf /opt/jboss/keycloak/standalone/configuration/standalone_xml_history/current
RUN /opt/jboss/keycloak/bin/add-user-keycloak.sh -r master -u admin -p admin