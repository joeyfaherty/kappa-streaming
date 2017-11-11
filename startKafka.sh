#!/bin/bash

# Util script to launch a console kafka + zookeeper
# Expects kafka installation to be in KAFKA_HOME directory

# if less than two arguments supplied, display usage
	if [  $# -le 0 ]
	then
		echo -e "Usage:\n$ ./startKafka.sh KAFKA_TOPIC_NAME\n"
		exit 1
	fi

$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &
sleep 5

$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties &
sleep 5

$KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic $1 &
