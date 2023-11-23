#!/usr/bin/env groovy

def devoptymize_clone(Script scriptRef, String gitURLString, String branchID, String gitUserID) {
    dir("devoptymize"){
        scriptRef.checkout([
            $class: 'GitSCM', 
            branches: [[name: branchID]], 
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'CleanCheckout']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[credentialsId: gitUserID, url: gitURLString]]
        ])
    }
}
def child_clone(Script scriptRef, String gitURLString, String branchID, String gitUserID) {
    dir("child"){
        scriptRef.checkout([
            $class: 'GitSCM', 
            branches: [[name: branchID]], 
            doGenerateSubmoduleConfigurations: false, 
            extensions: [[$class: 'CleanCheckout']], 
            submoduleCfg: [], 
            userRemoteConfigs: [[credentialsId: gitUserID, url: gitURLString]]
        ])
    }
}
def copy(String resource){
    sh  """ mkdir -p child/services/$resource
            cp -pR devoptymize/services/$resource/* child/services/$resource/
            mkdir -p child/services/$resource/tfvars/
            mkdir -p child/services/$resource/tfstate/
    """
}

def env_dir(String resource) {
    def list = ["tfstate","tfvars"]
    // check tfsate and tfvars dir exist or not
    for(item in list){
        d = sh(script: "test -d child/services/${resource}/${item}/${env.ENVIRONMENT} && echo '1' || echo '0' ", returnStdout: true).trim()
        if(d=='1'){
            echo "directory present"
        }
        else {
            sh  """ 
                mkdir -p child/services/$resource/${item}/${env.ENVIRONMENT}
            """
        }
    }

}

def call() {
    script{
        dir ("terraform") {
            child_clone(this,"https://gitlab.cloudifyops.com/ops_devoptymize/ops_terraform_templates.git","main","devoptymize")
            // check resource dir exist or not 
            f = sh(script: "test -d child/services/${env.resource} && echo '1' || echo '0' ", returnStdout: true).trim()
            if(f=='1'){
                echo "${env.resource} directory present"
                env_dir("${env.resource}")
            }
            else {
                devoptymize_clone(this,"https://gitlab.cloudifyops.com/devoptymize_infrastructure/devoptymize_terraform_templates.git","main","devoptymize")
                copy("${env.resource}")
                env_dir("${env.resource}")
            }
        }
    }
}
