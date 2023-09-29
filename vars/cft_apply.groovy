#!/usr/bin/env groovy
def call(){
   script{
      def stack = readFile "${env.ENVIRONMENT}/${env.resource_name}-${env.aws_region}.yaml"
      timeout(time: 300, unit: 'SECONDS'){
         input message: "Do you want to create the stack?",
         parameters: [text(name: 'Stack YAML', description: 'Please review the yaml file', defaultValue: stack)]
      }
      if ((env.resource == "aws-vpc-config") || (env.resource == "aws-eks-cdn") || (env.resource == "aws-3t-arch") || (env.resource == "eks") || (env.resource == "iam") || (env.resource == "aws-cicd-sw") || (env.resource == "aws-cicd-ms")) {
         sh """
         aws cloudformation create-stack --stack-name ${env.ENVIRONMENT}-$resource_name-${env.resource} --template-body file://${env.ENVIRONMENT}/${env.resource_name}-${env.aws_region}.yaml --capabilities CAPABILITY_NAMED_IAM
         """
      } else {
         sh """
         aws cloudformation create-stack --stack-name ${env.ENVIRONMENT}-$resource_name-${env.resource} --template-body file://${env.ENVIRONMENT}/${env.resource_name}-${env.aws_region}.yaml
         """
      }
      cft_stack_status_check ()
   }
}
