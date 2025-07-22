pipeline {
    agent any

    parameters {
        booleanParam(name: 'ROLLBACK', defaultValue: false, description: 'Rollback to previous deployment')
        booleanParam(name: 'KEEP_BACKUP', defaultValue: true, description: 'Do you want to keep the backup?')
    }

    stages {
        stage('Set Build Name') {
            steps {
                script {
                    def user = env.BUILD_USER_ID ?: 'Unknown User'
                    currentBuild.displayName = "${user}"
                    echo "Build name set to: ${currentBuild.displayName}"
                }
            }
        }

        stage('Build with Gradle') {
            when {
                expression { !params.ROLLBACK }
            }
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Move JAR to ec2-user directory') {
            when {
                expression { !params.ROLLBACK }
            }
            steps {
                script {
                    def jarFile = sh(script: "ls build/libs/GVS-0.0.1-SNAPSHOT.jar", returnStdout: true).trim()
                    echo "Moving JAR: ${jarFile} to /home/ec2-user/gvs-server"

                    sh """
                        sudo -u ec2-user bash -c '
                    if [ -f /home/ec2-user/gvs-server/GVS-0.0.1-SNAPSHOT.jar ]; then
                        ${params.KEEP_BACKUP ? "sudo mv /home/ec2-user/gvs-server/GVS-0.0.1-SNAPSHOT.jar /home/ec2-user/gvs-server/GVS-0.0.1-SNAPSHOT-revoke.jar" : "sudo rm -f /home/ec2-user/gvs-server/GVS-0.0.1-SNAPSHOT.jar"}
                    fi
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
                    def result = sh(
                            script: "sudo -u ec2-user fuser -k 8443/tcp || true",
                            returnStatus: true
                    )

                    if (result == 0) {
                        echo "Process on port 8443 killed successfully (or nothing running)."
                    } else {
                        echo "No process running or failed to kill process on port 8443."
                    }
                }
            }
        }


        stage('Run the JAR') {
            when {
                expression { !params.ROLLBACK }
            }
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


        stage('Rollback to Previous Deployment') {
            when {
                expression { params.ROLLBACK }
            }
            steps {
                script {
                    sh """
                        sudo -u ec2-user bash -c '
                        cd /home/ec2-user/gvs-server &&
                        source .bash_profile > /dev/null 2>&1 &&
                        [ ! -f application.log ] && sudo touch application.log &&
                        sudo chmod 666 application.log &&
                        sudo chmod 755 /home/ec2-user/gvs-server &&
                        nohup java -jar GVS-0.0.1-SNAPSHOT-revoke.jar > application.log 2>&1 &'
                    """
                    echo "Rollback deployment started successfully in the background."
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
    }
}