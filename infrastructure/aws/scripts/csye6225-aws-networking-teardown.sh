#!/bin/sh
set -e
echo 'Please enter name'
read name
VpcName=$name-Vpc
Subnet1=$name-Subnet1
Subnet2=$name-Subnet2
Subnet3=$name-Subnet3
GatewayName=$name-Gateway
RouteTableName=$name-RouteTable

VpcId=`aws ec2 describe-tags --filters "Name=value, Values=$VpcName" --query 'Tags[0].ResourceId' --output text`
Subnet1Id=`aws ec2 describe-tags --filters "Name=value, Values=$Subnet1" --query 'Tags[0].ResourceId' --output text`
Subnet2Id=`aws ec2 describe-tags --filters "Name=value, Values=$Subnet2" --query 'Tags[0].ResourceId' --output text`
Subnet3Id=`aws ec2 describe-tags --filters "Name=value, Values=$Subnet3" --query 'Tags[0].ResourceId' --output text`
GatewayId=`aws ec2 describe-tags --filters "Name=value, Values=$GatewayName" --query 'Tags[0].ResourceId' --output text`
RouteTableId=`aws ec2 describe-tags --filters "Name=value, Values=$RouteTableName" --query 'Tags[0].ResourceId' --output text`

########DELETE SUBNETS
aws ec2 delete-subnet --subnet-id $Subnet1Id
aws ec2 delete-subnet --subnet-id $Subnet2Id
aws ec2 delete-subnet --subnet-id $Subnet3Id
echo 'All subnets deleted successfully!'

########DELETE ROUTE
aws ec2 delete-route --route-table-id $RouteTableId --destination-cidr-block 0.0.0.0/0
echo 'Route deleted successfully!'

########DELETE ROUTE TABLE
aws ec2 delete-route-table --route-table-id $RouteTableId
echo 'Route Table deleted successfully!'

########DETACH AND DELETE INTERNET GATEWAY
aws ec2 detach-internet-gateway --internet-gateway-id $GatewayId --vpc-id $VpcId
echo 'Internet gateway detached successfully!'
aws ec2 delete-internet-gateway --internet-gateway-id $GatewayId
echo 'Internet gateway deleted successfully!'

########DELETE VPC
aws ec2 delete-vpc --vpc-id $VpcId
echo 'VPC deleted successfully!'

echo 'ALL SET!!!'
