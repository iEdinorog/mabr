pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'jdk'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    credentialsId: '2d727f96-c50e-422a-8532-c166d7f06731',
                    url: 'https://github.com/iEdinorog/mabr.git'
            }
        }
        stage('Check version') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn jib:build'
            }
        }
    }
    post {
        success {
            echo 'Build and push successful'
        }
        failure {
            echo 'Build failed'
        }
    }
}
