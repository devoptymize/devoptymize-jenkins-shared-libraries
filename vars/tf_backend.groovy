#!/usr/bin/env groovy

def call(String projectname, String resource, String resourcename, String environment, String accountid){
    sh """
cat <<EOF > tfstate/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.config
key = "$environment/$resource/${env.aws_region}/${resourcename}.tfstate"
# Common for security scripts
bucket = "$projectname-$accountid-$environment-terraform-statefile-bucket"
dynamodb_table = "$projectname-$accountid-$environment-terraform-lock-db"
encrypt        = true
region = "us-east-1"
EOF
"""
}
