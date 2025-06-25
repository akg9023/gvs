pipeline {
    agent any

    stages {
        stage('Hello World') {
            steps {
                sh 'mvn clean install'
            }
        }
    }
}
