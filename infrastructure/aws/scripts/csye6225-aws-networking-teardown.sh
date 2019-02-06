#!/bin/bash


echo Deleting networking resources using AWS CLI ...
aws ec2 describe-vpcs --query 'Vpcs[*].{VpcId:VpcId,Default:IsDefault}'

read -p "Enter the Vpc Id to be deleted: " vpcId

routeTable1=`aws ec2 describe-route-tables --filters "Name=vpc-id,Values=$vpcId" --query 'RouteTables[0].RouteTableId' --output text`
routeTable2=`aws ec2 describe-route-tables --filters "Name=vpc-id,Values=$vpcId" --query 'RouteTables[1].RouteTableId' --output text`

aws ec2 describe-route-tables

read -p "Enter the Routetable Id to be deleted: " routeTable2

echo Deleting Route Table $routeTable2 with $vpcId ...
aws ec2 delete-route-table --route-table-id $routeTable2


internetGateway=`aws ec2 describe-internet-gateways --filters "Name=attachment.vpc-id,Values=$vpcId" --query 'InternetGateways[*].InternetGatewayId' --output text`
echo Deleting Internet Gateway $internetGateway with $vpcId ...
aws ec2 detach-internet-gateway --internet-gateway-id $internetGateway --vpc-id $vpcId
aws ec2 delete-internet-gateway --internet-gateway-id $internetGateway

aws ec2 delete-vpc --vpc-id $vpcId
echo Vpc $vpcId is successfully deleted!
