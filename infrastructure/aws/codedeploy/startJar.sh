#!/bin/bash
cd ~
url=$(cat mysqlsetting.txt | sed -r 's/.*"(.+)".*/\1/')
cd ~/webapp/csye6225/target
java -jar csye6225-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --spring.datasource.url=jdbc:mysql://$url/csye6225 --server.port=8080 >~/webapp/csye6225/target/log/ 2>&1 &
