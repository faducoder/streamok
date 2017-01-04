FROM java
RUN mkdir -p /opt/kafka
RUN cd /opt/kafka
RUN wget http://www.mirrorservice.org/sites/ftp.apache.org/kafka/0.9.0.1/kafka_2.11-0.9.0.1.tgz
RUN tar xpf kafka_2.11-0.9.0.1.tgz
RUN chmod -R a=u /opt/kafka
WORKDIR /opt/kafka
VOLUME /tmp/kafka-logs /tmp/zookeeper
EXPOSE 2181 2888 3888 9092