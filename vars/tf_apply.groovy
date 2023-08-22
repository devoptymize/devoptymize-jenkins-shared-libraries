#!/usr/bin/env groovy

def call(){
   script{
      sh """   terraform init --reconfigure --backend-config=tfstate/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.config
         terraform plan -out=tfplan -var-file=tfvars/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.tfvars
         terraform show -no-color tfplan > tfplan.txt
      """
      def plan = readFile 'tfplan.txt'
      timeout(time: 300, unit: 'SECONDS'){
         input message: "Do you want to apply the plan?",
         parameters: [text(name: 'Plan', description: 'Please review the plan', defaultValue: plan)]
      }
      sh """ terraform apply -auto-approve -var-file="tfvars/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.tfvars" """
   }
}
