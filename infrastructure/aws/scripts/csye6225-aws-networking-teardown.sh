#!/bin/bash


echo Deleting vpc, below is the Vpcs:
aws ec2 describe-vpcs --query 'Vpcs[*].{VpcId:VpcId,Default:IsDefault}'

read -p "Please enter the VpcId you want to delete: " vpcId

routeTable1=`aws ec2 describe-route-tables --filters "Name=vpc-id,Values=$vpcId" --query 'RouteTables[0].RouteTableId' --output text`
routeTable2=`aws ec2 describe-route-tables --filters "Name=vpc-id,Values=$vpcId" --query 'RouteTables[1].RouteTableId' --output text`

aws ec2 describe-route-tables

read -p "Please enter the RoutetableId you want to delete: " routeTable2

echo Start to delete Route Table $routeTable2 associated with $vpcId ...
aws ec2 delete-route-table --route-table-id $routeTable2


internetGateway=`aws ec2 describe-internet-gateways --filters "Name=attachment.vpc-id,Values=$vpcId" --query 'InternetGateways[*].InternetGatewayId' --output text`
echo Start to delete Internet Gateway $internetGateway associated with $vpcId ...
aws ec2 detach-internet-gateway --internet-gateway-id $internetGateway --vpc-id $vpcId
aws ec2 delete-internet-gateway --internet-gateway-id $internetGateway

aws ec2 delete-vpc --vpc-id $vpcId
echo Vpc $vpcId is deleted!
