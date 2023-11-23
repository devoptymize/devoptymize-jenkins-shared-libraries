def call(){
    script{
        sh """ 
        terraform init --reconfigure --backend-config=tfstate/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.config
        terraform plan -destroy -out=tfdestroy  -var-file="tfvars/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.tfvars"
        terraform show -no-color tfdestroy > tfdestroy.txt
        """
        def destroy = readFile 'tfdestroy.txt'
        timeout(time: 300, unit: 'SECONDS'){
            input message: "Do you want to apply the plan?",
            parameters: [text(name: 'Destroy', description: 'Please review the plan', defaultValue: destroy)]
        }
        sh """ terraform apply "tfdestroy" """
        sh """
            rm ./tfvars/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.tfvars
            rm ./tfstate/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.config
            aws s3 rm s3://"${env.PROJECT_NAME}-${env.ACCOUNT_ID}-${env.ENVIRONMENT}-terraform-statefile-bucket/${env.ENVIRONMENT}/${env.resource}/${env.aws_region}/${env.resource_name}.tfstate"
        """

    }
}
