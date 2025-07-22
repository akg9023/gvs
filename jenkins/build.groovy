pipeline {
    agent any

    stages {
//        stage('Build with Gradle') {
//            steps {
//                sh 'chmod +x ./gradlew'
//                sh './gradlew clean build'
//            }
//        }

        stage('Run the JAR') {
            steps {
                script {
                    def jarFile = sh(script: "ls build/libs/GVS-0.0.1-SNAPSHOT.jar", returnStdout: true).trim()
                    echo "Running JAR: ${jarFile}"

                    sh """
                        sudo -u ec2-user bash -c '
                        cd /home/ec2-user/gvs-server &&
                        source .bash_profile > /dev/null 2>&1 &&
                        nohup java -jar GVS-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &'
                    """
                    echo "Application started successfully in the background."
                }
            }
        }
        stage('Wait for Assertion') {
            steps {
                script {
                    try {
                        timeout(time: 2, unit: 'MINUTES') {
                            waitUntil {
                                def conditionMet = sh(script: "sudo -u ec2-user bash -c 'curl -s http://localhost:8443/health | grep UP'", returnStatus: true) == 0
                                if (conditionMet) {
                                    echo "Assertion passed: Application is healthy."
                                    return true
                                }
                                echo "Waiting for application to become healthy..."
                                sleep(time: 10, unit: 'SECONDS') // Poll every 10 seconds
                                return false
                            }
                        }
                    } catch (Exception e) {
                        error "Assertion failed: Application did not become healthy within 2 minutes."
                    }
                }
            }
        }
    }
}