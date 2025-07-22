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
                        sudo cp "${env.WORKSPACE}/${jarFile}" /home/ec2-user/gvs-server/
                        sudo chown ec2-user:ec2-user /home/ec2-user/gvs-server/GVS-0.0.1-SNAPSHOT.jar'
                    """
                    echo "JAR moved successfully to /home/ec2-user/gvs-server"
                }
            }
        }

        stage('Kill Process on Port 8443') {
            steps {
                script {
                    def pid = sh(script: 'sudo -u ec2-user bash -c "lsof -i :8443 | awk \'NR==2 {print \\$2}\'"', returnStdout: true).trim()
                    if (pid) {
                        echo "Stopping process on port 8443 with PID: ${pid}"
                        sh "sudo -u ec2-user bash -c 'kill -9 ${pid}'"
                        echo "Process on port 8443 killed successfully."
                    } else {
                        echo "No process running on port 8443."
                    }
                }
            }
        }

        stage('Run the JAR') {
            steps {
                script {

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