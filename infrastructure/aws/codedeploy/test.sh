#!/bin/bash
echo "start"
sudo service tomcat stop
cd ~
sudo chmod +x csye6225-0.0.1-SNAPSHOT.jar
mysql -h csye6225-spring2019.ckwry3x5tnw1.us-east-1.rds.amazonaws.com -ucsye6225master -p'csye6225password' < sql.sql
exit
nohup java -jar csye6225-0.0.1-SNAPSHOT.jar --spring.active.profile=dev --spring.datasource.url=jdbc:mysql://csye6225-spring2019.ckwry3x5tnw1.us-east-1.rds.amazonaws.com/csye6225  --server.port=8080 &