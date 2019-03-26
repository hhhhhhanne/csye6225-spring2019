echo "Enter your stack name:"
read name

echo "Will create ec2 instance from image id: $imageId"
echo "your stack name is :" $name

echo "Enter your target networking stack name:"
read network
echo "your networking stack name is:" $network

vpcTag=$network'-csye6225-vpc'
webappSGTag=$network'-csye6225-WebAppSecurityGroup'
webappSubnetTag=$network'-csye6225-WebAppSubnet'
dbSGTag=$network'-csye6225-WebAppDBSecurityGroup'
dbSubnetTag=$network'-csye6225-DBSubnet'

imageId=`aws ec2 describe-images --owners self --query "Images[?Description=='Centos AMI for CSYE 6225 - Spring 2019'].ImageId" --output text`
vpcId=`aws ec2 describe-tags --filters "Name=value,Values=$vpcTag" --query "Tags[0].ResourceId" --output text`
webappSecurityGroup=`aws ec2 describe-tags --filters "Name=value,Values=$webappSGTag" --query "Tags[0].ResourceId" --output text`
webappSubnetId=`aws ec2 describe-tags --filters "Name=value,Values=$webappSubnetTag" --query "Tags[0].ResourceId" --output text`
dbSecurityGroup=`aws ec2 describe-tags --filters "Name=value,Values=$dbSGTag" --query "Tags[0].ResourceId" --output text`
dbSubnetId=`aws ec2 describe-tags --filters "Name=value,Values=$dbSubnetTag" --query "Tags[0].ResourceId" --output text`
dbSubnetGroup=`aws ec2 describe-tags --filters "Name=value,Values=*-dbSubnetGroup" --query "Tags[0].ResourceId" --output text`

aws cloudformation create-stack --stack-name $name --template-body file://csye6225-cf-application.json --capabilities CAPABILITY_NAMED_IAM --parameters ParameterKey=StackName,ParameterValue=$name ParameterKey=AMIInageId,ParameterValue=$imageId ParameterKey=VPCId,ParameterValue=$vpcId ParameterKey=AppSubNetId,ParameterValue=$webappSubnetId ParameterKey=DbSubNetId,ParameterValue=$dbSubnetId ParameterKey=AppSecurityGroupId,ParameterValue=$webappSecurityGroup ParameterKey=DBSecurityGroupId,ParameterValue=$dbSecurityGroup
aws cloudformation wait stack-create-complete --stack-name $name
echo "Stack $name successfully created"