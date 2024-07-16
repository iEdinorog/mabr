pipeline {
    agent {
        docker {
            image 'maven:3.8.6-openjdk-18'
            args '-v /root/.m2:/root/.m2'
        }
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
