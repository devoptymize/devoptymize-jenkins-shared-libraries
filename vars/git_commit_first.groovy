def call(){
  script {
    dir ("child_terrafrom") {
      git branch: "main", credentialsId: 'devoptymize', url: "*********************"
      url1 = sh(returnStdout: true, script: 'git config remote.origin.url').trim()
      url = url1.substring(8)
      catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
      withCredentials([usernamePassword(credentialsId: 'devoptymize', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
        def encodedPassword = URLEncoder.encode("$GIT_PASSWORD",'UTF-8')
        sh "git config user.email devoptymize"
        sh "git config user.name devoptymize"
        sh "git checkout main"
        sh """
        cat <<EOF > .gitignore
# Local .terraform directories
**/.terraform/*
**/.terraform.lock.hcl
**.pem
# Include tfplan, tfdestroy files to ignore the plan output of command: terraform plan -out=tfplan
tfplan
tfplan.txt
tfdestroy
tfdestroy.txt
EOF
        """
        sh "git add ."
        sh "git commit -m 'Auto commited to child repo with updated tfvars: ${env.BUILD_NUMBER}'"
        sh "git push https://${GIT_USERNAME}:${encodedPassword}@${url}"
        }
      }
    }
  }
}
