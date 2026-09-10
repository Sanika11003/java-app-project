pipeline {
 
    agent any
 
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-amazon-corretto'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }
 
    stages {
 
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Sanika11003/java-app-project'
            }
        }
 
        stage('Verify Tools') {
            steps {
                sh '''
                    echo "JAVA_HOME=$JAVA_HOME"
                    java -version
                    mvn -version
                '''
            }
        }
        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }
 
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }
 
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
 
        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }
 
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }
    } 
    
    post {
 
        success {
 
            echo "Pipeline Completed Successfully"
 
        }
 
        failure {
 
            echo "Pipeline Failed"
 
        }
 
        always {
 
            echo "Pipeline Finished"
 
        }
 
    }
}
