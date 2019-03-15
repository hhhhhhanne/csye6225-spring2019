#!/bin/bash
echo "start jar"
cd ~
sudo chmod +x csye6225-0.0.1-SNAPSHOT.jar
nohup java -jar csye6225-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --spring.datasource.url=jdbc:mysql://csye6225-spring2019.cudakpsmqak8.us-east-1.rds.amazonaws.com/csye6225  --server.port=8080 &
