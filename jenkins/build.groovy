pipeline {
    agent any

    stages {
        stage('Hello World') {
            steps {
                sh 'echo "Hello from Shell inside Jenkins!"'
            }
        }
    }
}
