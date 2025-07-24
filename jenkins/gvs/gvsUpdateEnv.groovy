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
                                    echo "Updating \$key in env"
                                    sudo sed -i "s|^export \$key=.*|export \$key=\$value|" .bash_profile
                                else
                                    echo "Adding \$key to env"
                                    echo "export \$key=\$value" | sudo tee -a .bash_profile > /dev/null
                                fi
                                source .bash_profile
                                # Validate the environment variable
                                if env | grep -q "^${key}=${value}"; then
                                    echo "Success: Environment variable \\$key=\\$value has been set."
                                else
                                    echo "Failure: Environment variable \\$key=\\$value was not set."
                                    exit 1
                                fi
                            done
                            '
                        """
                        sh(updateScript)
                    } else {
                        echo "No environment variables provided. Skipping update."
                    }
                }
            }
        }
    }
}