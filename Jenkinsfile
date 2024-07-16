pipeline {
    agent any
    tools {
        maven 'maven'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/your-repo/spring-boot-app.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
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
