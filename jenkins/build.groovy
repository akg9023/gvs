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
                    sh '''
                        sudo -u ec2-user bash -c "
                            source /home/ec2-user/gvs-server/.bash_profile &&
                            pid=$(lsof -t -i:8443) &&
                            if [ ! -z \\"$pid\\" ]; then
                                echo \\"Stopping process on port 8443 with PID: $pid\\" &&
                                echo "kill"
                                echo \\"Sleeping for 2 minutes...\\" &&
                                sleep 120;
                   
                            else
                                echo \\"No process running on port 8443\\";
                            fi &&
                            java -jar build/libs/GVS-0.0.1-SNAPSHOT.jar
                        "
                    '''
                    // Find the generated JAR file
//                    def jarFile = sh(script: "ls build/libs/GVS-0.0.1-SNAPSHOT.jar", returnStdout: true).trim()
//                    echo "Running JAR: ${jarFile}"
//                    sh "sudo su ec2-user"  // switch to ec2-user
//                    sh  "source /home/ec2-user/gvs-server/.bash_profile"  // set env variable
//
//                    // find the application and
//                    def pid = sh(script: "lsof -i :8443 | awk 'NR==2 {print \$2}'", returnStdout: true).trim()
//
//                    if (pid) {
//                        echo "Stopping process on port 8443 with PID: ${pid}"
////                        sh "kill -9 ${pid}"
//                    } else {
//                        echo "No process running on port 8443"
//                        error("Pipeline terminated: No process running on port 8443")
//                    }
//                    sh "java -jar ${jarFile}"
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
