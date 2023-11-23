multibranchPipelineJob("${PROJECT_NAME}_Config_Multibranch") {
    branchSources {
        branchSource {
            source {
                git {
                    id('12113456788')
                    remote('https://gitlab.cloudifyops.com/devoptymize_infrastructure/devoptymize_jenkins_config.git')
                    credentialsId('devoptymize')
                    traits {
                        gitBranchDiscovery()
                        headWildcardFilter {
                            // Space-separated list of name patterns to consider.
                            includes(' 1.Create_AWS_Secrets 2.Create_S3_DynamoDB ')
                            // Space-separated list of name patterns to ignore even if matched by the includes list.
                            excludes('main')
                        }
                    }
                }
            }
        }
    }
    
}
