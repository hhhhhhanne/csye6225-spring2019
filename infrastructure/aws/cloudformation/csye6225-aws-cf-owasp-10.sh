echo "Enter your stack name:"
read name
echo "your stack name is :" $name
aws cloudformation create-stack --stack-name $name --template-body file://owasp_10_base.yml
aws cloudformation wait stack-create-complete --stack-name $name
name=$name'nocsrf'
aws cloudformation create-stack --stack-name $name --template-body file://owasp_10_base_no_csrf.yml