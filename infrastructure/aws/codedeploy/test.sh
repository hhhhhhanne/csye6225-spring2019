#!/bin/bash
sudo service tomcat stop
kill -9 $(lsof -i:8080 |awk '{print $2}' | tail -n 2)
cd ~
url=$(cat mysqlsetting.txt | sed -r 's/.*"(.+)".*/\1/')
mysql -h $url -ucsye6225master -p'csye6225password' < sql.sql
sudo mkdir -p ~/webapp/csye6225/target/src/main/resources
sudo chmod 777 -R ~/webapp/csye6225/target/src/main/resources
sudo mkdir -p ~/webapp/csye6225/target/log
cd ~/webapp/csye6225/target/log
sudo touch springboot.log
sudo chmod 777 -R ~/webapp/csye6225/target/log/springboot.log
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/centos/cloudwatch-config.json -s
sudo systemctl restart amazon-cloudwatch-agent.service