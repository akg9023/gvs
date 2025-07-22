pipeline {
    agent any

//    environment {
//        JAR_NAME = 'GVS-0.0.1-SNAPSHOT.jar' // Update if your JAR name is different
//        WORKSPACE_JAR_PATH = "build/libs/${JAR_NAME}"
//        TARGET_DIR = '/home/ec2-user/gvs-server'
//    }

    stages {
        stage('Run JAR as ec2-user using Python') {
            steps {
                sh """
        sudo -u ec2-user bash -c '
        cd /home/ec2-user/gvs-server
        source ~/.bash_profile
        java -jar GVS-0.0.1-SNAPSHOT.jar
        '
    """
            }


            // stage('Clean Workspace') {
            //     steps {
            //         echo "Cleaning Jenkins workspace..."
            //         deleteDir() // Deletes all files in the current workspace
            //     }
            // }

//        stage('Checkout') {
//            steps {
//                // Code already checked out via SCM in Jenkins job
//                echo "Repo checked out at: ${env.WORKSPACE}"
//            }
//        }
//
//        stage('Build with Gradle') {
//            steps {
//                // Go up from jenkins/ to project root
//                    sh 'chmod +x ./gradlew'
//                    sh './gradlew clean build'
//            }
//        }

//        stage('Move JAR to ec2-user directory') {
//            steps {
//                sh """
//                    su - ec2-user -c '
//                     cp ${WORKSPACE_JAR_PATH} ${TARGET_DIR}/
//                     chown ec2-user:ec2-user ${TARGET_DIR}/${JAR_NAME}
//                    '
//                """
//            }
//        }
//
//        stage('Run JAR as ec2-user') {
//            steps {
//                sh """
//                    su - ec2-user -c '
//                    cd ${TARGET_DIR}
//                    source ~/.bash_profile
//                    nohup java -jar ${JAR_NAME} > app.log 2>&1 &
//                    '
//                """
//            }
//        }

//        stage('Run the JAR') {
//            steps {
//                script {
//
////                     Find the generated JAR file
//                    def jarFile = sh(script: "ls build/libs/GVS-0.0.1-SNAPSHOT.jar", returnStdout: true).trim()
//                    echo "Running JAR: ${jarFile}"
////                    sh 'source /var/lib/jenkins/app.env > /dev/null 2>&1' // Suppress output
//
//                    // find the application and
//                    def pid = sh(script: "lsof -i :8443 | awk 'NR==2 {print \$2}'", returnStdout: true).trim()
////
//                    if (pid) {
//                        echo "Stopping process on port 8443 with PID: ${pid}"
////                        sh "kill -9 ${pid}"
//                    } else {
//                        echo "No process running on port 8443"
//                    }
//
//                    // Start the application and save logs to app.log
////                    sh "source /var/lib/jenkins/app.env > /dev/null 2>&1 && setsid nohup java -jar ${jarFile} > app.log 2>&1 & disown"
//                    sh """
//                        screen -dmS gvs-server bash -c '
//                        source /var/lib/jenkins/app.env > /dev/null 2>&1
//                        java -jar ${jarFile} > app.log 2>&1
//                        '
//                    """
//
////                    sh """
////                        sudo -u ec2-user bash -c '
////                        cd /home/ec2-user/gvs-server && \
////                        source .bash_profile > /dev/null 2>&1 && \
////                        java -jar GVS-0.0.1-SNAPSHOT.jar > app.log 2>&1 &'
////                    """
//                    echo "Application started successfully in the background. Monitoring logs..."
//
//                    // Monitor the log file for success or error
//                    def success = false
//                    def errorLine = null
//                    try {
//                        timeout(time: 120, unit: 'SECONDS') { // Set a timeout for monitoring
//                            waitUntil {
//                                def logContent = readFile('app.log')
//                                echo logContent // Print the log content incrementally
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
//                    // Handle success or error
//                    if (success) {
//                        sleep(time: 120, unit: 'SECONDS') // Wait for a few seconds to ensure the app is fully started
//                        echo "Application started successfully."
//                    } else if (errorLine) {
//                        error "Application failed with error: ${errorLine}"
//                    } else {
//                        error "Application did not start successfully."
//                    }
//                }
//            }
//        }

            // stage('Archive JAR') {
            //     steps {
            //         archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            //     }
            // }
        }
    }
}