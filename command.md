# Install Zookeper

- docker run -d --name zookeeper -p 2181:2181 --restart=unless-stopped wurstmeister/zookeeper

# Install Kafka

docker run -d --name kafka -p 9092:9092 -e KAFKA_ADVERTISED_HOST_NAME=192.168.100.183 -e KAFKA_ZOOKEEPER_CONNECT=192.168.149.128:2181 --restart=unless-stopped wurstmeister/kafka

- docker exec -it kafka /bin/bash
- cd /opt/kafka/bin/
- kafka-topics.sh --create --topic test --zookeper 192.168.149.128:2181 --partitions 1 --replication-factor 1

# IP ADDRESS

- 192.168.100.183
