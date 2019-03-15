#!/bin/bash
cd ~
pwd
cd '~/webapp/csye6225/target'
pwd
ls
sudo chmod +x csye6225-0.0.1-SNAPSHOT.jar
java -jar csye6225-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --spring.datasource.url=jdbc:mysql://csye6225-spring2019.cudakpsmqak8.us-east-1.rds.amazonaws.com/csye6225  --server.port=8080
