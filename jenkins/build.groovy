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

         stage('Run the JAR') {
            steps {
                script {
                    // Find the generated JAR file
                    def jarFile = sh(script: "ls build/libs/GVS-0.0.1-SNAPSHOT.jar", returnStdout: true).trim()
                    echo "Running JAR: ${jarFile}"
                    
                    // Run the JAR
                    sh "nohup java -jar ${jarFile} > /dev/null 2>&1 &"
                    
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
