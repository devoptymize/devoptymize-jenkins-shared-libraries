#!/usr/bin/env groovy

def call() {
    script{
        sh "sudo rm -rf ${env.WORKSPACE}/*"
    }
}