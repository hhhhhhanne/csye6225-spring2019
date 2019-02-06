#!/bin/bash


echo Creating VPC ...
vpcId=`aws ec2 create-vpc --cidr-block 10.0.0.0/16 --query 'Vpc.VpcId' --output text`
echo VPC $vpcId is successfully created!

echo Creating Internet Gateway ...
internetGatewayId=`aws ec2 create-internet-gateway --query 'InternetGateway.InternetGatewayId' --output text`
echo Internet GW $internetGatewayId created!

echo Attaching IGW to VPC ...
aws ec2 attach-internet-gateway --internet-gateway-id $internetGatewayId --vpc-id $vpcId
echo IGW is successfully attached to VPC $vpcId !

echo Creating Route Table ...
routeTableId=`aws ec2 create-route-table --vpc-id $vpcId --query 'RouteTable.RouteTableId' --output text`
echo Adding Default Route to $routeTableId ...

aws ec2 create-route --route-table-id $routeTableId --destination-cidr-block 0.0.0.0/0 --gateway-id $internetGatewayId


echo Vpc $vpcId is successfully created!
