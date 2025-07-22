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

                    def pid = sh(script: "lsof -i :8443 | awk 'NR==2 {print \$2}'", returnStdout: true).trim()
                    if (pid) {
                        echo "Stopping process on port 8443 with PID: ${pid}"
                    } else {
                        echo "No process running on port 8443"
                    }


                    sh """
                        sudo -u ec2-user bash -c '
                        cd /home/ec2-user/gvs-server && \
                        nohup java -jar GVS-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &'
                    """
                    sleep(time: 120, unit: 'SECONDS')
                    echo "Application started successfully in the background."

//                    def success = false
//                    def errorLine = null
//                    try {
//                        timeout(time: 120, unit: 'SECONDS') {
//                            waitUntil {
//                                def logContent = readFile('app.log')
//                                echo logContent
//                                if (logContent.contains("Started HlzRegApplication")) {
//                                    success = true
//                                    return true
//                                }
//                                def errorMatch = logContent =~ /(?i)Failed/
//                                if (errorMatch) {
//                                    errorLine = errorMatch[0]
//                                    return true
//                                }
//                                return false
//                            }
//                        }
//                    } catch (Exception e) {
//                        error "Timeout reached while monitoring logs."
//                    }
//
//                    if (success) {
//                        sleep(time: 120, unit: 'SECONDS')
//                        echo "Application started successfully."
//                    } else if (errorLine) {
//                        error "Application failed with error: ${errorLine}"
//                    } else {
//                        error "Application did not start successfully."
//                    }
                }
            }
        }
    }
}