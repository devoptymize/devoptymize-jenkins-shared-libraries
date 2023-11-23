#!/usr/bin/env groovy

def call(String jinja2File)  {
    script {
        dir ("""cloudformation/child/services/${env.resource}""") {
            // withCredentials is the function to configure the AWS creds to environment by fetching creds from AWS Secret Manager
            withCredentials([string(credentialsId: "${env.PROJECT_NAME.toLowerCase()}_${env.ACCOUNT_ID}_aws_cred", variable: 'secret')]) {
                def creds = readJSON text: secret
                env.AWS_ACCESS_KEY_ID = creds['accessKeyId']
                env.AWS_SECRET_ACCESS_KEY = creds['secretAccessKey']

                if(env.ACTION == "Create") {                            
                    // ctf_generator() will create the .ctfvars file
                    workspace = pwd()
                    pwd()
                    cft_generator.generate_var(params,workspace) 
                    def script_render_template = libraryResource 'render-templates.py'
                    writeFile file: "${env.WORKSPACE}/render-templates.py", text: script_render_template
                    sh """
                        /usr/bin/python3 ${env.WORKSPACE}/render-templates.py ${jinja2File} variable.yaml "${env.ENVIRONMENT}/${env.resource_name}-${env.aws_region}.yaml"
                    """
                    cft_apply()
                }
                else{
                    cft_destroy()
                }
            }   
            //}                   
        }
    }
} 
