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
                        sudo mv "${env.WORKSPACE}/${jarFile}" /home/ec2-user/gvs-server/
                        sudo chown ec2-user:ec2-user /home/ec2-user/gvs-server/GVS-0.0.1-SNAPSHOT.jar'
                    """
                    echo "JAR moved successfully to /home/ec2-user/gvs-server"
                }
            }
        }

        stage('Kill Process on Port 8443') {
            steps {
                script {
                    def pid = sh(script: "sudo -u ec2-user bash -c 'lsof -i :8443 | awk \\\"NR==2 {print \\\$2}\\\"'", returnStdout: true).trim()
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
                        [ ! -f application.log ] && sudo touch application.log &&
                        sudo chmod 666 application.log &&
                        sudo chmod 755 /home/ec2-user/gvs-server &&
                        nohup java -jar GVS-0.0.1-SNAPSHOT.jar > application.log 2>&1 &'
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
                                // Print the last 15 lines of the log file
                                sh "sudo -u ec2-user bash -c 'tail -n 15 /home/ec2-user/gvs-server/application.log'"

                                // Check if the log contains the desired keyword
                                def logContainsStarted = sh(script: "sudo -u ec2-user bash -c 'grep -q \"Started HlzRegApplication\" /home/ec2-user/gvs-server/application.log'", returnStatus: true) == 0
                                if (logContainsStarted) {
                                    echo "Application log contains 'Started HlzRegApplication'. Application is running successfully."
                                    return true
                                }
                                echo "Waiting for 'Started HlzRegApplication' message in application.log..."
                                sleep(time: 10, unit: 'SECONDS') // Poll every 10 seconds
                                return false
                            }
                        }
                    } catch (Exception e) {
                        error "Application did not log 'Started HlzRegApplication' within the timeout period."
                    } finally {
                        // Delete the application.log file
                        sh "sudo -u ec2-user bash -c 'sudo rm -f /home/ec2-user/gvs-server/application.log'"
                        echo "application.log file deleted."
                    }
                }
            }
        }
//        stage('Verify Application Running') {
//            steps {
//                script {
//                    try {
//                        timeout(time: 2, unit: 'MINUTES') {
//                            waitUntil {
//                                def isRunning = sh(script: "sudo -u ec2-user bash -c 'ss -tuln | grep :8443'", returnStatus: true) == 0
//                                if (isRunning) {
//                                    echo "Application is running on port 8443."
//                                    return true
//                                }
//                                echo "Waiting for application to start on port 8443..."
//                                sleep(time: 10, unit: 'SECONDS') // Poll every 10 seconds
//                                return false
//                            }
//                        }
//                    } catch (Exception e) {
//                        error "Application did not start on port 8443 within the timeout period."
//                    }
//                }
//            }
//        }
    }
}