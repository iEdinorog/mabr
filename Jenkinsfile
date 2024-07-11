pipeline {
    agent any

	environment {
		mavenHome = tool 'maven'
	}

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
                bat "mvn clean compile jib:build"
            }
        }
    }
}
