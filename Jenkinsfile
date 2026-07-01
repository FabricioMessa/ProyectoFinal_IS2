pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh './mvnw clean compile'
            }
        }
        stage('Functional Tests') {
            steps {
                sh './mvnw test -Dtest="FunctionalTests"'
            }
        }
        stage('API Tests') {
            steps {
                sh 'npx newman run src/test/resources/postman/ProductSystemAPI.postman_collection.json'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'target/surefire-reports/*.xml', fingerprint: true
        }
    }
}