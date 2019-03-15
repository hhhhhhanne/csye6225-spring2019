#!/bin/bash
sudo service tomcat stop
cd ~
mysql -h csye6225-spring2019.cudakpsmqak8.us-east-1.rds.amazonaws.com -ucsye6225master -p'csye6225password' < sql.sql
