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

                    def pid = null
                    try {
                        timeout(time: 2, unit: 'MINUTES') {
                            waitUntil {
                                pid = sh(script: "ssh ec2-user@3.228.158.146 lsof -i :8443 | awk 'NR==2 {print \$2}'", returnStdout: true).trim()
                                if (pid) {
                                    echo "Application is running with PID: ${pid}"
                                    return true
                                }
                                echo "Waiting for application to start..."
                                sleep(time: 10, unit: 'SECONDS') // Poll every 10 seconds
                                return false
                            }
                        }
                    } catch (Exception e) {
                        error "Application did not start within the timeout period."
                    }

                    if (pid) {
                        echo "Application started successfully with PID: ${pid}. Terminating Jenkins job."
                        currentBuild.result = 'SUCCESS'
                    } else {
                        error "Application failed to start."
                    }
                }
            }
        }
    }
}