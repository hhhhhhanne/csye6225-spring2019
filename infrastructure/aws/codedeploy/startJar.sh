#!/bin/bash
cd ~
cd ~/webapp/csye6225/target
url=$(cat mysqlsetting.txt | sed -r 's/.*"(.+)".*/\1/')
java -jar csye6225-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --spring.datasource.url=jdbc:mysql://$url/csye6225  --server.port=8080 >/dev/null 2>&1 &
