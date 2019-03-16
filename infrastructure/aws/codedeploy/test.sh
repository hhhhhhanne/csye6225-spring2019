#!/bin/bash
sudo service tomcat stop
cd ~
url=$(cat mysqlsetting.txt | sed -r 's/.*"(.+)".*/\1/')
mysql -h $url -ucsye6225master -p'csye6225password' < sql.sql
