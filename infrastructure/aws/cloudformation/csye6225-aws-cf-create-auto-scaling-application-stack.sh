echo "Enter your stack name:"
read name

echo "Will create ec2 instance from image id: $imageId"
echo "your stack name is :" $name

echo "Enter your target networking stack name:"
read network
echo "your networking stack name is:" $network

echo "Enter your domain name (skip csye6225-spring2019- and .me), only your name please:"
read username
echo "your networking stack name is: "'csye6225-spring2019-'$username'.me'

vpcTag=$network'-csye6225-vpc'
webappSGTag=$network'-csye6225-WebAppSecurityGroup'
webappSubnetTag=$network'-csye6225-WebAppSubnet'
dbSGTag=$network'-csye6225-WebAppDBSecurityGroup'
dbSubnetTag=$network'-csye6225-DBSubnet'
domainName='csye6225-spring2019-'$username'.me.'

imageId=`aws ec2 describe-images --owners self --query "Images[?Description=='Centos AMI for CSYE 6225 - Spring 2019'].ImageId" --output text`
vpcId=`aws ec2 describe-tags --filters "Name=value,Values=$vpcTag" --query "Tags[0].ResourceId" --output text`
webappSecurityGroup=`aws ec2 describe-tags --filters "Name=value,Values=$webappSGTag" --query "Tags[0].ResourceId" --output text`
webappSubnetId=`aws ec2 describe-tags --filters "Name=value,Values=$webappSubnetTag" --query "Tags[0].ResourceId" --output text`
dbSecurityGroup=`aws ec2 describe-tags --filters "Name=value,Values=$dbSGTag" --query "Tags[0].ResourceId" --output text`
dbSubnetId=`aws ec2 describe-tags --filters "Name=value,Values=$dbSubnetTag" --query "Tags[0].ResourceId" --output text`
dbSubnetGroup=`aws ec2 describe-tags --filters "Name=value,Values=*-dbSubnetGroup" --query "Tags[0].ResourceId" --output text`
hostedZoneId=`aws route53 list-hosted-zones --query "HostedZones[?Name=='csye6225-spring2019-liuchangsi.me.'].Id" --output text`
echo "your hostedZoneId is "$hostedZoneId

aws cloudformation create-stack --stack-name $name --template-body file://csye6225-cf-auto-scaling-application.json --capabilities CAPABILITY_NAMED_IAM --parameters ParameterKey=StackName,ParameterValue=$name ParameterKey=AMIInageId,ParameterValue=$imageId ParameterKey=VPCId,ParameterValue=$vpcId ParameterKey=AppSubNetId,ParameterValue=$webappSubnetId ParameterKey=DbSubNetId,ParameterValue=$dbSubnetId ParameterKey=AppSecurityGroupId,ParameterValue=$webappSecurityGroup ParameterKey=DBSecurityGroupId,ParameterValue=$dbSecurityGroup ParameterKey=UserDomainName,ParameterValue=$domainName ParameterKey=HostedZoneId,ParameterValue=$hostedZoneId
aws cloudformation wait stack-create-complete --stack-name $name