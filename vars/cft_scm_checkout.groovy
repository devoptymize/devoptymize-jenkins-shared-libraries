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
            ls child/services/$resource/
    """
}

def env_dir(String resource) {

        d = sh(script: "test -d child/services/${resource}/${env.ENVIRONMENT} && echo '1' || echo '0' ", returnStdout: true).trim()
        if(d=='1'){
            echo "directory present"
        }
        else {
            sh  """ 
                mkdir -p child/services/$resource/${env.ENVIRONMENT}
            """
        }
    }

def call() {
    script{
        dir ("cloudformation") {
            child_clone(this,"<child-repo-url-goes-here>","main","devoptymize")
            // check resource dir exist or not 
            f = sh(script: "test -d child/services/${env.resource} && echo '1' || echo '0' ", returnStdout: true).trim()
            if(f=='1'){
                echo "${env.resource} directory present"
                devoptymize_clone(this,"https://github.com/devoptymize/devoptymize-cloudformation-templates.git","main","devoptymize")
                copy("${env.resource}")
                env_dir("${env.resource}")
            }
            else {
                devoptymize_clone(this,"https://github.com/devoptymize/devoptymize-cloudformation-templates.git","main","devoptymize")
                copy("${env.resource}")
                env_dir("${env.resource}")
            }
        }
    }
}
