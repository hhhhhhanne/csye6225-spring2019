echo "Enter your CI/CD stack name:"
read name
echo "your CI/CD stack name is :" $name

aws cloudformation create-stack --stack-name $name --template-body file://csye6225-cf-cicd.json --capabilities CAPABILITY_NAMED_IAM --parameters ParameterKey=StackName,ParameterValue=$name

aws cloudformation wait stack-create-complete --stack-name $name
echo "Stack $name successfully created"