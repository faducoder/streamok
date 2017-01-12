FROM openjdk:8u111-jre

RUN wget http://www.mirrorservice.org/sites/ftp.apache.org/kafka/0.10.1.1/kafka_2.11-0.10.1.1.tgz
RUN tar xpf kafka_2.11-0.10.1.1.tgz
RUN mv /kafka_2.11-0.10.1.1 /opt/kafka
RUN chmod -R a=u /opt/kafka
VOLUME /tmp/kafka-logs /tmp/zookeeper
EXPOSE 2181 2888 3888 9092