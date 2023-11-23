def call(){
  script {
    dir ("cloudformation/child/services/${env.resource}") {
      url1 = sh(returnStdout: true, script: 'git config remote.origin.url').trim()
      url = url1.substring(8)
      catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
      withCredentials([usernamePassword(credentialsId: 'devoptymize', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
        def encodedPassword = URLEncoder.encode("$GIT_PASSWORD",'UTF-8')
        sh "ls"
        sh "echo $url"
        sh "git config user.email devoptymize"
        sh "git config user.name devoptymize"
        sh "git checkout main"
        sh '''
          if [ "$ACTION" = "Create" ]; then
            git add $ENVIRONMENT/$resource_name-$aws_region.yaml
          else
            git add . 
          fi
        '''
        sh "git commit -m 'Auto commited to child repo with updated cft stack.yaml: ${env.BUILD_NUMBER}'"
        sh "git push https://${GIT_USERNAME}:${encodedPassword}@${url}"
        }
      }
    }
  }
}
