#!/bin/sh
set -e

echo 'Please enter name!'
read name
##############CREATE VPC AND GET VPCID
VpcId=`aws ec2 create-vpc --cidr-block 10.0.0.0/16 --query 'Vpc.VpcId' --output text`
aws ec2 create-tags --resources $VpcId --tags Key=Name,Value=$name-Vpc
echo 'VPC created successfully!'
##############GET AVAILABILITY-ZONES FROM US-EAST-1
zone1=`aws ec2 describe-availability-zones --region us-east-1 --query 'AvailabilityZones[0].ZoneId' --output text`
zone2=`aws ec2 describe-availability-zones --region us-east-1 --query 'AvailabilityZones[2].ZoneId' --output text`
zone3=`aws ec2 describe-availability-zones --region us-east-1 --query 'AvailabilityZones[4].ZoneId' --output text`

##############CREATE SUBNET
subnet1=`aws ec2 create-subnet --availability-zone-id $zone1 --vpc-id $VpcId --cidr-block 10.0.1.0/24 --query 'Subnet.SubnetId' --output text`
aws ec2 create-tags --resources $subnet1 --tags Key=Name,Value=$name-Subnet1
echo 'first subnet created successfully!'
subnet2=`aws ec2 create-subnet --availability-zone-id $zone2 --vpc-id $VpcId --cidr-block 10.0.16.0/24 --query 'Subnet.SubnetId' --output text`
aws ec2 create-tags --resources $subnet2 --tags Key=Name,Value=$name-Subnet2
echo 'second subnet created successfully!'
subnet3=`aws ec2 create-subnet --availability-zone-id $zone3 --vpc-id $VpcId --cidr-block 10.0.32.0/24 --query 'Subnet.SubnetId' --output text`
aws ec2 create-tags --resources $subnet3 --tags Key=Name,Value=$name-Subnet3
echo 'third subnet created successfully!'
##############CREATE INTERNET GATEWAY
gatewayId=`aws ec2 create-internet-gateway --query 'InternetGateway.InternetGatewayId' --output text`
aws ec2 create-tags --resources $gatewayId --tags Key=Name,Value=$name-Gateway
echo 'Internet gateway created successfully!'

##############ATTACH INTERNET GATEWAY TO VPC
aws ec2 attach-internet-gateway --internet-gateway-id $gatewayId --vpc-id $VpcId
echo 'Internet gateway attached successfully!'

##############CREATE ROUTE TABLE
routeTableId=`aws ec2 create-route-table --vpc-id $VpcId --query 'RouteTable.RouteTableId' --output text`
aws ec2 create-tags --resources $routeTableId --tags Key=Name,Value=$name-RouteTable
echo 'route table created successfully!'

##############ASSOCIATE ALL SUBNETS TO ROUTE TABLE
aws ec2 associate-route-table --route-table-id $routeTableId --subnet-id $subnet1
aws ec2 associate-route-table --route-table-id $routeTableId --subnet-id $subnet2
aws ec2 associate-route-table --route-table-id $routeTableId --subnet-id $subnet3
echo 'all subnet attached successfully!'

#CREATE ROUTE
aws ec2 create-route --route-table-id $routeTableId --destination-cidr-block 0.0.0.0/0 --gateway-id $gatewayId
echo 'route created successfully!'

##############MODIFY SECURITY GROUP
#DELETE ALL DEFAULT RULES
groupId=`aws ec2 describe-security-groups --filters "Name=vpc-id, Values=$VpcId" --query 'SecurityGroups[0].GroupId' --output text`

ipPermissions=`aws ec2 describe-security-groups --group-id $groupId --query 'SecurityGroups[0].IpPermissions'`

ipPermissionsEgress=`aws ec2 describe-security-groups --group-id $groupId --query 'SecurityGroups[0].IpPermissionsEgress' --output json`
if [ "$ipPermissions"x != "[]"x ]; then
echo $ipPermissions > ipPermissions.txt
aws ec2 revoke-security-group-ingress --group-id $groupId --ip-permissions file://ipPermissions.txt
rm -f ipPermissions.txt
fi
if [ "$ipPermissionsEgress"x != "[]"x ]; then
echo $ipPermissionsEgress > ipPermissionsEgress.txt
aws ec2 revoke-security-group-egress --group-id $groupId --ip-permissions file://ipPermissionsEgress.txt
rm -f ipPermissionsEgress.txt
fi

#ADD NEW RULES
aws ec2 authorize-security-group-ingress --group-id $groupId --protocol tcp --port 22 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-ingress --group-id $groupId --protocol tcp --port 80 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-egress --group-id $groupId --protocol tcp --port 22 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-egress --group-id $groupId --protocol tcp --port 80 --cidr 0.0.0.0/0
