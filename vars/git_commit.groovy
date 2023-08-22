def call(){
  script {
    dir ("terraform/child/") {
      url1 = sh(returnStdout: true, script: 'git config remote.origin.url').trim()
      url = url1.substring(8)
      catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
      withCredentials([usernamePassword(credentialsId: 'devoptymize', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
        def encodedPassword = URLEncoder.encode("$GIT_PASSWORD",'UTF-8')
        sh "echo $url"
        sh "git config user.email devoptymize"
        sh "git config user.name devoptymize"
        sh "git checkout main"
        sh "git add ."        
        sh "git commit -m 'Auto commited to child repo with updated tfvars: ${env.BUILD_NUMBER}'"
        sh "git push https://${GIT_USERNAME}:${encodedPassword}@${url}"
        }
      }
    }
  }
}
