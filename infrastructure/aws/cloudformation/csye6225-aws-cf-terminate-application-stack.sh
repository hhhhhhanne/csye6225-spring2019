echo "Enter your stack name: "
read name
aws cloudformation delete-stack --stack-name $name
aws cloudformation stack-delete-complete --stack0name $name
echo "Stack $name deleted!"
