pipeline {
    agent any
    stages {

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

    }
}