pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "mtiriwissem/java-app"
        GIT_REPO = "https://github.com/wissemmtiri/DevOps-TP2.git"
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
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
        stage('Deploy on Kubernetes') {
            steps {
                script {
                    sh '''
                        export KUBECONFIG=/var/jenkins_home/kube
                        kubectl set image deployment/web-app-depl web-application=${DOCKER_IMAGE} --record
                    '''
                }
            }
        }
        stage('Rollout Status') {
            steps {
                script {
                    sh '''
                        export KUBECONFIG=/var/jenkins_home/kube
                        kubectl rollout status deployment/web-app-depl
                    '''
                }
            }
        }
        // stage('Deploy with Ansible') {
        //     steps {
        //         withCredentials([sshUserPrivateKey(credentialsId: 'vm-ssh-credentials', keyFileVariable: 'SSH_KEY')]) {
        //             script {
        //                 sh '''
        //                     ansible-playbook -i ansible/inventory.ini ansible/playbook.yml \
        //                                      --private-key $SSH_KEY \
        //                                      -e "ansible_ssh_common_args='-o StrictHostKeyChecking=no'"
        //                 '''
        //             }
        //         }
        //     }
        // }
    }
}
