pipeline {

    agent any

    environment {
        COMMIT_MESSAGE = getCommitMessage()
    }

    libraries {
        lib('pac-pipeline-libraries@1.16.3')
    }

    stages {

        stage('Build and push to artifactory') {
            when {
                not {
                    branch 'master'
                }
            }
            steps {
                mvn 'clean deploy -P instrumentation'
            }
            post {
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    runSonar()
                }
            }
        }

        stage('Release Artifact') {
            when {
                expression {
                    BRANCH_NAME == 'master' && !COMMIT_MESSAGE.startsWith('[maven-release-plugin]')
                }
            }
            steps {
                generateChangelog()
                mvnRelease()
            }
            post {
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    runSonar()
                }
            }
        }
    }
}
