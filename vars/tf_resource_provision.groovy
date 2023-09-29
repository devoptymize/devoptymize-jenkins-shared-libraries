#!/usr/bin/env groovy

def call() {
    script {
        dir ("""terraform/child/services/${env.resource}""") {
            // withCredentials is the function to configure the AWS creds to environment by fetching creds from AWS Secret Manager
            withCredentials([string(credentialsId: "${env.PROJECT_NAME.toLowerCase()}_${env.ACCOUNT_ID}_aws_cred", variable: 'secret')]) {
                def creds = readJSON text: secret
                env.AWS_ACCESS_KEY_ID = creds['accessKeyId']
                env.AWS_SECRET_ACCESS_KEY = creds['secretAccessKey']

                if(env.ACTION == "Create") {                            
                    // tf_backend() creates the backend file
                    tf_backend(env.PROJECT_NAME, env.resource, env.resource_name, env.ENVIRONMENT, env.ACCOUNT_ID)
                    // tf_generator() will create the .tfvars file
                    workspace = pwd()
                    tf_generator.generate_var(params,workspace)                            
                    // tf_apply() tf init, plan and take a approval from the user and apply
                    tf_apply()
                }
                else if(env.ACTION == "Modify"){ 
                    // tf_generator() will create the .tfvars file
                    workspace = pwd()
                    tf_generator.generate_var(params,workspace) 
                    // tf_apply() tf init, plan and take a approval from the user and apply
                    tf_apply()
                }
                else{
                    tf_destroy()
                }
            }                   
        }
    }
}
