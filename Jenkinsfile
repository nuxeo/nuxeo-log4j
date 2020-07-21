/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     Julien Carsique <jcarsique@nuxeo.com>
 */

void setGitHubBuildStatus(String context) {
    step([
            $class       : 'GitHubCommitStatusSetter',
            reposSource  : [$class: 'ManuallyEnteredRepositorySource', url: 'https://github.com/nuxeo/nuxeo-log4j'],
            contextSource: [$class: 'ManuallyEnteredCommitContextSource', context: context],
            errorHandlers: [[$class: 'ShallowAnyErrorHandler']]
    ])
}

String getMavenArgs() {
    def args = '-V -B clean install'
    if (env.TAG_NAME) {
        args += ' -Prelease deploy -DskipTests'
    }
    return args
}

pipeline {
    agent {
        label "jenkins-ai-nuxeo11"
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(daysToKeepStr: '60', numToKeepStr: '60', artifactNumToKeepStr: '5'))
        timeout(time: 1, unit: 'HOURS')
    }
    environment {
        ORG = 'nuxeo'
        branch_name_lower_case = "${env.BRANCH_NAME.toLowerCase()}"
    }
    stages {
        stage('Build') {
            environment {
                MAVEN_OPTS = "$MAVEN_OPTS -Xms512m -Xmx1g"
                MAVEN_ARGS = getMavenArgs()
                AWS_REGION = "us-east-1"
            }
            steps {
                setGitHubBuildStatus('build')
                container('platform11') {
                    sh 'mvn ${MAVEN_ARGS}'
                    sh "find . -name '*-reports' -type d"
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/*-reports/*.xml'
                    archiveArtifacts artifacts: '**/log/*.log, **/nxserver/config/distribution.properties, ' +
                            '**/target/*-reports/*, **/target/results/*.html, **/target/*.png, **/target/*.html',
                            allowEmptyArchive: true
                    setGitHubBuildStatus('build')
                }
            }
        }
    }
    post {
        always {
            script {
                if (env.BRANCH_NAME == 'master') {
                    step([$class: 'JiraIssueUpdater', issueSelector: [$class: 'DefaultIssueSelector'], scm: scm])
                }
            }
        }
    }
}
