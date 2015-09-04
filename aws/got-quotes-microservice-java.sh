#!/bin/bash
set -e

sudo mkdir /opt/got-quotes-microservice-java
sudo tar -xvf /opt/got-quotes-microservice-java.tar -C /opt/got-quotes-microservice-java
sudo mv /opt/got-quotes-microservice-java/got-quotes-microservice-java*/* /opt/got-quotes-microservice-java

sudo chown -R root:root /opt/got-quotes-microservice-java
sudo chmod -R 644 /opt/got-quotes-microservice-java

sudo chown -R root:root /opt/got-quotes-microservice-java
sudo chmod 755 /opt/got-quotes-microservice-java/bin/got-quotes-microservice-java

sudo mkdir /var/log/got-quotes-microservice-java
