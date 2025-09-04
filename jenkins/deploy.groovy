pipeline {
    agent {
        kubernetes {
            label 'docker-kubectl-agent'
        }
    }

    parameters {
        string(
            name: 'VERSION',
            defaultValue: 'latest',
            description: 'Enter the version tag from DockerHub'
        )
    }

    environment {
        DOCKER_IMAGE = "orvencasido/resume-project-k8s-3"
    }

    stages {
        stage('Pull') {
            steps {
                container('docker') {
                    script {
                        echo "Pulling image: ${DOCKER_IMAGE}:${params.VERSION}"
                        sh "docker pull ${DOCKER_IMAGE}:${params.VERSION}"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                container('kubectl') {
                    script {
                        sh """
                            kubectl apply -f resume-deployment.yaml -n default
                            kubectl apply -f resume-service.yaml -n default
                            kubectl set image deployment/resume resume=${DOCKER_IMAGE}:${VERSION} -n default
                            kubectl rollout status deployment/resume -n default
                        """
                    }
                }
            }
        }
    }
}
