pipeline {
    agent { 
        kubernetes {
            label 'docker-kubectl-agent'
        }
    }

    parameters {
        string(
            name: 'VERSION',
            defaultValue: '',
            description: 'Specify the version tag (default: 1.BUILD_NUMBER)'
        )
    }

    environment {
        DOCKER_IMAGE = "orvencasido/resume-project-k8s-3"
        VERSION = "${params.VERSION ?: "1.${env.BUILD_NUMBER}"}"
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
    }
}
