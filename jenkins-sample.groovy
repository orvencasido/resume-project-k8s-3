pipeline {
    agent {
        kubernetes {
            label 'docker-kubectl-agent'
        }
    }
    
    stages {
        stage('test') {
            steps {
                container('docker') {
                    script {
                        sh """
                            docker ps 
                        """
                    }
                }
                
                container('kubectl') {
                    sh 'kubectl get pods -n jenkins '
                }
            }
        }
    }
}