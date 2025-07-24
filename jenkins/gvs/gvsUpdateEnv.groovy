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
                            touch .bash_profile
                            for pair in ${envVars.join(' ')}; do
                                key=\$(echo \$pair | cut -d= -f1)
                                value=\$(echo \$pair | cut -d= -f2)
                                if grep -q "^export \$key=" .bash_profile; then
                                    sed -i "s|^export \$key=.*|export \$key=\$value|" .bash_profile
                                else
                                    echo "export \$key=\$value" >> .bash_profile
                                fi
                            done
                            source .bash_profile
                            '
                        """
                        sh updateScript
                    } else {
                        echo "No environment variables provided. Skipping update."
                    }
                }
            }
        }
    }
}