pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Code already checked out via SCM in Jenkins job
                echo "Repo checked out at: ${env.WORKSPACE}"
            }
        }

        stage('Build JAR with Gradle') {
            steps {
                dir("${env.WORKSPACE}") {
                    sh './gradlew clean build'
                }
            }
        }

        // stage('Archive JAR') {
        //     steps {
        //         archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        //     }
        // }
    }
}
