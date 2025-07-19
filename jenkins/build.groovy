pipeline {
    agent any

    stages {
        // stage('Clean Workspace') {
        //     steps {
        //         echo "Cleaning Jenkins workspace..."
        //         deleteDir() // Deletes all files in the current workspace
        //     }
        // }

        stage('Checkout') {
            steps {
                // Code already checked out via SCM in Jenkins job
                echo "Repo checked out at: ${env.WORKSPACE}"
            }
        }

        stage('Build with Gradle') {
            steps {
                // Go up from jenkins/ to project root
                dir("${env.WORKSPACE}") {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build'
                }
            }
        }

        stage('Run the JAR') {
            steps {
                script {

//                     Find the generated JAR file
                    def jarFile = sh(script: "ls build/libs/GVS-0.0.1-SNAPSHOT.jar", returnStdout: true).trim()
                    echo "Running JAR: ${jarFile}"
                    sh 'source /var/lib/jenkins/.bash_profile > /dev/null 2>&1' // Suppress output

                    // find the application and
                    def pid = sh(script: "lsof -i :8443 | awk 'NR==2 {print \$2}'", returnStdout: true).trim()

                    if (pid) {
                        echo "Stopping process on port 8443 with PID: ${pid}"
//                        sh "kill -9 ${pid}"
                    } else {
                        echo "No process running on port 8443"
                    }
                    sh "source /var/lib/jenkins/.bash_profile > /dev/null 2>&1 && export \$(cat /var/lib/jenkins/.bash_profile | xargs) && java -jar ${jarFile}"
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
