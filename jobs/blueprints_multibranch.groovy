multibranchPipelineJob("${PROJECT_NAME}_BluePrint_Multibranch") {
    branchSources {
        branchSource {
            source {
                git {
                    id('123456656789')
                    remote('https://github.com/devoptymize/devoptymize-jenkins-pipelines.git')
                    credentialsId('devoptymize')
                    traits {
                        gitBranchDiscovery()
                        headWildcardFilter {
                            // Space-separated list of name patterns to consider.
                            includes('Devoptymize-aws*')
                            // Space-separated list of name patterns to ignore even if matched by the includes list.
                            excludes('main')
                        }
                    }
                }
            }
        }
    }
}
