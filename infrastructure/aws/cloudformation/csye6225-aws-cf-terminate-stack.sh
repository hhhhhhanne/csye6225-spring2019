echo "Enter your stack name: "
read name
aws cloudformation delete-stack --stack-name $name
