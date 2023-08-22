def call(){
   script{
      timeout(time: 300, unit: 'SECONDS'){
         input message: "Do you want to delete the stack $ENVIRONMENT-$resource_name-$resource"
      }
      sh """  
        aws cloudformation delete-stack --stack-name $ENVIRONMENT-$resource_name-$resource --region "$REGION"
      """
      cft_stack_status_check ()
      sh "rm -f ${env.ENVIRONMENT}/${env.resource_name}-${env.aws_region}.yaml "
   }
}
