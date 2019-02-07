#!/bin/sh
echo 'Please enter VPC name!'
read VpcName
##############CREATE VPC AND GET VPCID
VpcId=`aws ec2 create-vpc --cidr-block 10.0.0.0/16 --query 'Vpc.VpcId' --output text`
aws ec2 create-tags --resources $VpcId --tags Key=Name,Value=$VpcName
echo 'VPC created successfully!'
##############GET AVAILABILITY-ZONES FROM US-EAST-1
zone1=`aws ec2 describe-availability-zones --region us-east-1 --query 'AvailabilityZones[0].ZoneId' --output text`
zone2=`aws ec2 describe-availability-zones --region us-east-1 --query 'AvailabilityZones[2].ZoneId' --output text`
zone3=`aws ec2 describe-availability-zones --region us-east-1 --query 'AvailabilityZones[4].ZoneId' --output text`

##############CREATE SUBNET
subnet1=`aws ec2 create-subnet --availability-zone-id $zone1 --vpc-id $VpcId --cidr-block 10.0.1.0/24 --query 'Subnet.SubnetId' --output text`
echo 'first subnet created successfully!'
subnet2=`aws ec2 create-subnet --availability-zone-id $zone2 --vpc-id $VpcId --cidr-block 10.0.16.0/24 --query 'Subnet.SubnetId' --output text`
echo 'second subnet created successfully!'
subnet3=`aws ec2 create-subnet --availability-zone-id $zone3 --vpc-id $VpcId --cidr-block 10.0.32.0/24 --query 'Subnet.SubnetId' --output text`
echo 'third subnet created successfully!'
##############CREATE INTERNET GATEWAY
gatewayId=`aws ec2 create-internet-gateway --query 'InternetGateway.InternetGatewayId' --output text`
echo 'Internet gateway created successfully!'

##############ATTACH INTERNET GATEWAY TO VPC
aws ec2 attach-internet-gateway --internet-gateway-id $gatewayId --vpc-id $VpcId
echo 'Internet gateway attached successfully!'

##############CREATE ROUTE TABLE
routeTableId=`aws ec2 create-route-table --vpc-id $VpcId --query 'RouteTable.RouteTableId' --output text`
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
#groupId=`aws ec2 describe-security-groups --group-names default --query 'GroupId' --output text`
#ipPermissions=`aws ec2 describe-security-groups --group-id $groupId --query 'IpPermissions' --output text`
#ipPermissionsEgress=`aws ec2 describe-security-groups --group-id $groupId --query 'IpPermissionsEgress' --output text`
#aws ec2 revoke-security-group-ingress --group-id $groupId --ip-permissions $ipPermissions
#aws ec2 revoke-security-group-ingress --group-id $groupId --ip-permissions $ipPermissionsEgress
#ADD NEW RULES
#aws ec2 authorize-security-group-ingress --group-id sg-b5502cf0 --protocol tcp --port 22 --cidr 0.0.0.0/0
#aws ec2 authorize-security-group-ingress --group-id $groupId --protocol tcp --port 80 --cidr 0.0.0.0/0
#aws ec2 authorize-security-group-egress --group-id sg-b5502cf0 --protocol tcp --port 22 --cidr 0.0.0.0/0
#aws ec2 authorize-security-group-egress --group-id $groupId --protocol tcp --port 80 --cidr 0.0.0.0/0
