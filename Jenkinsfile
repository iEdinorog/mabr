pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', 
                    credentialsId: 'Jenkins',
                    url:'https://github.com/iEdinorog/mabr.git'
                
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean compile jib:build'
            }
        }
    }
}
