echo "Enter your stack name:"
read name
echo "your stack name is :" $name
aws cloudformation create-stack --stack-name $name --template-body file://csye6225-cf-networking.json --parameters "ParameterKey=StackName,ParameterValue=$name"
