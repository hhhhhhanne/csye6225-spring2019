echo "Enter your CI/CD stack name:"
read name
echo "your CI/CD stack name is :" $name

echo "Enter your name for S3 bucket:"
read bucketname
echo "your CodeDeploy S3 bucket is: code-deploy.csye6225-spring2019-"$bucketname".me"

aws cloudformation create-stack --stack-name $name --template-body file://csye6225-cf-cicd.json --capabilities CAPABILITY_NAMED_IAM --parameters ParameterKey=StackName,ParameterValue=$name ParameterKey=BucketName,ParameterValue=$bucketname

aws cloudformation wait stack-create-complete --stack-name $name
echo "Stack $name successfully created"