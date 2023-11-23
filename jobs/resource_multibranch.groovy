listView("${PROJECT_NAME}_DevOptymize") {
    jobs {
        name("${PROJECT_NAME}_Resource_Multibranch")
        name("${PROJECT_NAME}_Config_Multibranch")
        name("${PROJECT_NAME}_BluePrint_Multibranch")
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
        
    }
}

multibranchPipelineJob("${PROJECT_NAME}_Resource_Multibranch") {
    branchSources {
        branchSource {
            source {
                git {
                    id('123456788')
                    remote('https://gitlab.cloudifyops.com/devoptymize_infrastructure/devoptymize_jenkins_pipelines.git')
                    credentialsId('devoptymize')
                    traits {
                        gitBranchDiscovery()
                        headWildcardFilter {
                            // Space-separated list of name patterns to consider.
                            includes('*.Devoptymize-*')
                            // Space-separated list of name patterns to ignore even if matched by the includes list.
                            excludes('main')
                        }
                    }
                }
            }
        }
    }
}
