# Install Zookeper

- docker run -d --name zookeeper -p 2181:2181 --restart=unless-stopped wurstmeister/zookeeper

# Install Kafka

docker run -d --name kafka -p 9092:9092 -e KAFKA_ADVERTISED_HOST_NAME=192.168.100.183 -e KAFKA_ZOOKEEPER_CONNECT=192.168.149.128:2181 --restart=unless-stopped wurstmeister/kafka

- docker exec -it kafka /bin/bash
- cd /opt/kafka/bin/
- kafka-topics.sh --create --topic test --zookeper 192.168.149.128:2181 --partitions 1 --replication-factor 1

# IP ADDRESS

- 192.168.100.183

bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
bin/kafka-console-consumer.sh --topic email-topic --from-beginning --bootstrap-server localhost:9092

docker run -e KEYCLOAK_USER=d2y -e KEYCLOAK_PASSWORD=31072001 jboss/keycloak
