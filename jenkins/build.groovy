pipeline {
    agent any

    stages {
        stage('Build jar') {
            steps {
                sh 'mvn clean install"'
            }
        }

    }
}
