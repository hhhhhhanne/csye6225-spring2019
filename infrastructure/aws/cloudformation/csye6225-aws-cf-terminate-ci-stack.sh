echo "Enter your stack name: "
read name
aws cloudformation delete-stack --stack-name $name
aws cloudformation wait stack-delete-complete --stack-name $name
