echo "Enter your stack name:"
read name
imageId=`aws ec2 describe-images --owners self --query "Images[?Description=='Centos AMI for CSYE 6225 - Spring 2019'].ImageId" --output text`
echo "Will create ec2 instance from image id: $imageId"
echo "your stack name is :" $name
aws cloudformation create-stack --stack-name $name --template-body file://csye6225-cf-application.json --parameters ParameterKey=StackName,ParameterValue=$name ParameterKey=AMIInageId,ParameterValue=$imageId
aws cloudformation wait stack-create-complete --stack-name $name
echo "Stack $name successfully created"
