#!/bin/bash
echo "start"
sudo service tomcat stop
cd ~
sudo chmod +x csye6225-0.0.1-SNAPSHOT.jar
mysql -h csye6225-spring2019.cudakpsmqak8.us-east-1.rds.amazonaws.com -ucsye6225master -p'csye6225password' < sql.sql
exit
java -jar csye6225-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --spring.datasource.url=jdbc:mysql://csye6225-spring2019.cudakpsmqak8.us-east-1.rds.amazonaws.com/csye6225  --server.port=8080 > stop.txt 2>&1
