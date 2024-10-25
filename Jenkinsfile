pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "mtiriwissem/java-app"
        GIT_REPO = "https://github.com/wissemmtiri/DevOps-TP2.git"
        REPO_NAME = "DevOps-TP2"
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        TARGET_VM = "finetune-project@74.235.232.144"
        TARGET_PATH = "/home/finetune-project/deployment"
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: "${GIT_REPO}"
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', 
                                                 usernameVariable: 'DOCKER_USERNAME', 
                                                 passwordVariable: 'DOCKER_PASSWORD')]) {
                    script {
                        sh '''
                            echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                            docker push $DOCKER_IMAGE
                            docker logout
                        '''
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'vm-ssh-credentials', keyFileVariable: 'SSH_KEY')]) {
                    script {
                        // Run the Ansible playbook for deployment
                        sh '''
                            ansible-playbook -i ansible/inventory.ini ansible/deploy.yml \
                                             --private-key $SSH_KEY
                        '''
                    }
                }
            }
        }
    }
}
