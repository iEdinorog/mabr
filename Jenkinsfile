pipeline {
    agent {
	    docker {
		    image 'maven:3.8.6'
	    }
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
		        sh 'mvn clean compile jib:build';
            }
		}
    }
}
