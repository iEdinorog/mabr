pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url:'https://github.com/iEdinorog/mabr'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean compile jib:build'
            }
        }
    }
}