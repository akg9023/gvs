pipeline {
    agent any

    parameters {
        string(name: 'ENV_VARS', defaultValue: '', description: 'Environment variables in key=value format separated by semicolons')
    }

    stages {
        stage('Update env variables') {
            steps {
                script {
                    if (params.ENV_VARS?.trim()) {
                        def envVars = params.ENV_VARS.split(';')
                        def updateScript = """
                            sudo su ec2-user -c '
                            cd /home/ec2-user/gvs-server
                            [ -f .bash_profile ] || sudo touch .bash_profile
                            sudo chmod u+w .bash_profile
                            for pair in ${envVars.join(' ')}; do
                                key=\$(echo \$pair | cut -d= -f1)
                                value=\$(echo \$pair | cut -d= -f2)
                                if grep -q "^export \$key=" .bash_profile; then
                                    echo "Updating \$key in evn"
                                    sudo sed -i "s|^export \$key=.*|export \$key=\$value|" .bash_profile
                                else
                                    echo "Adding \$key to env"
                                    echo "export \$key=\$value" | sudo tee -a .bash_profile > /dev/null
                                fi
                                source .bash_profile
                              
                            done
                            '
                        """
                        sh updateScript

                        // Validate each key-value pair
                        envVars.each { pair ->
                            def key = pair.split('=')[0]
                            def value = pair.split('=')[1]
                            def result = sh(
                                    script: """
                                        sudo su ec2-user -c '
                                           cd /home/ec2-user/gvs-server &&
                                           source .bash_profile > /dev/null 2>&1 &&
                                           env | grep "^${key}=${value}"'
                                    """,
                                    returnStatus: true
                            )
                            if (result == 0) {
                                echo "Success: Environment variable ${key}=${value} has been added."
                            } else {
                                echo "Failure: Environment variable ${key}=${value} was not added."
                            }
                        }

                    } else {
                        echo "No environment variables provided. Skipping update."
                        def envOutput = sh(
                                script: """
                                        sudo su ec2-user -c '
                                           cd /home/ec2-user/gvs-server &&
                                           source .bash_profile > /dev/null 2>&1 &&
                                           env
                                    """,
                                returnStatus: true
                        )
                        echo "Current environment variables:\n${envOutput}"
                    }
                }
            }
        }
    }
}