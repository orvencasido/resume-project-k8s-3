pipeline {
    agent {
        kubernetes {
            label 'docker-kubectl-agent'
        }
    }

    environment {
        DOCKER_IMAGE = "orvencasido/resume-project-k8s-3"
        VERSION = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Build') {
            steps {
                container('docker') {
                    script {
                        sh "docker build -t ${DOCKER_IMAGE}:${VERSION} ."
                    }
                }
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-cred', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PW')]) {
                    container('docker') {
                        script {
                            sh "echo ${DOCKER_PW} | docker login -u ${DOCKER_USER} --password-stdin"
                            sh "docker push ${DOCKER_IMAGE}:${VERSION}"
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                container('kubectl') {
                    script {
                        sh """
                            kubectl delete pod -l app=resume -n default --ignore-not-found
                            kubectl set image deployment/resume resume=${DOCKER_IMAGE}:${VERSION} -n default
                            kubectl rollout status deployment/resume -n default
                        """
                    }
                }
            }
        }
    }
}
