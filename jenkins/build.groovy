pipeline {
    agent any

    stages {
        stage('Build with Gradle') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Move JAR to ec2-user directory') {
            steps {
                script {
                    def jarFile = sh(script: "ls build/libs/GVS-0.0.1-SNAPSHOT.jar", returnStdout: true).trim()
                    echo "Moving JAR: ${jarFile} to /home/ec2-user/gvs-server"

                    sh """
                        sudo -u ec2-user bash -c '
                        cp ${env.WORKSPACE}/${jarFile} /home/ec2-user/gvs-server/
                        chown ec2-user:ec2-user /home/ec2-user/gvs-server/${jarFile}'
                    """
                    echo "JAR moved successfully to /home/ec2-user/gvs-server"
                }
            }
        }

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
        stage('Verify Application Running') {
            steps {
                script {
                    try {
                        timeout(time: 2, unit: 'MINUTES') {
                            waitUntil {
                                def isRunning = sh(script: "sudo -u ec2-user bash -c 'ss -tuln | grep :8443'", returnStatus: true) == 0
                                if (isRunning) {
                                    echo "Application is running on port 8443."
                                    return true
                                }
                                echo "Waiting for application to start on port 8443..."
                                sleep(time: 10, unit: 'SECONDS') // Poll every 10 seconds
                                return false
                            }
                        }
                    } catch (Exception e) {
                        error "Application did not start on port 8443 within the timeout period."
                    }
                }
            }
        }
    }
}