packer build \
    -var 'aws_region=us-east-1' \
    -var 'subnet_id=subnet-041a15a1a9580a375' \
    centos-ami-template.json