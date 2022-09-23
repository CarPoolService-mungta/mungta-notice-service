pipeline {
  agent any
  environment {
    IMAGE_REPO = 'mungtaregistry.azurecr.io/mungta/dev'
    IMAGE_NAME = 'notice-service'
    IMAGE_TAG = "${env.BUILD_NUMBER}"
    //IMAGE_TAG = 'latest'
    ENVIRONMENT = 'dev'
    ARGOCD_APP = 'mungta-notice'
    APP_WAIT_TIMEOUT = '600'
  }
  stages {
    stage('Build & Unit Test') {
        steps {
            //slackSend (color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            sh './gradlew clean build'
        }
    }
//    stage('Unit Test') {
//        steps {
//            sh './gradlew test'
//        }
//        post {
//            always {
//                junit 'build/test-results/test/binary/*.xml'
//                step([ $class: 'JacocoPublisher' ])
//            }
//        }
//    }
    stage('Static Code Analysis') {
        steps {
            sh './gradlew sonarqube'
        }

    }
//    stage('Package') {
//        steps {
//            sh './mvnw package -DskipTests'
//            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
//        }
//    }
    stage('Build Docker image') {
        steps {
            echo 'The build number is ${IMAGE_TAG}'
            sh 'docker build --build-arg ENVIRONMENT=${ENVIRONMENT} -t ${IMAGE_REPO}/${IMAGE_NAME}:${IMAGE_TAG} .'
        }
    }
    stage('Push Docker image') {
        steps {
            withCredentials([azureServicePrincipal('azure_service_principal')]) {
                echo '---------az login------------'
                sh '''
                az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID
                az account set -s $AZURE_SUBSCRIPTION_ID
                '''
                sh 'az acr login --name mungtaregistry'
                sh 'docker push ${IMAGE_REPO}/${IMAGE_NAME}:${IMAGE_TAG}'
                sh 'az logout'
            }
        }
    }
    stage('Clean Docker image') {
        steps {
            echo '---------Clean image------------'
            sh 'docker rmi ${IMAGE_REPO}/${IMAGE_NAME}:${IMAGE_TAG}'
        }
    }
     stage('Update manifest') {
         steps {
           sh """
             git config --global user.name "${GITHUB_NAME}"
             git config --global user.email "${GITHUB_EMAIL}"
             git config --global credential.helper cache
             git config --global push.default simple
           """

           git url: "${HELM_CHART}", credentialsId: 'mungta_github_ssh', branch: 'main'
           sh """
             sed -i 's/tag:.*/tag: "${IMAGE_TAG}"/g' dev/notice/values.yaml
             git add dev/notice/values.yaml
             git commit -m 'Update Docker image tag: ${IMAGE_TAG}'
           """

           sshagent (credentials: ['mungta_github_ssh']) {
             sh 'git push origin main'
           }
         }
     }
//     stage('Argo Sync') {
//         steps {
//           withCredentials([usernamePassword(credentialsId: 'mungta_argocd', usernameVariable: 'ARGOCD_USER', passwordVariable: 'ARGOCD_AUTH_PWD')]) {
//             sh """
//             argocd login --insecure "${ArgoURL}" --username ${ARGOCD_USER} --password ${ARGOCD_AUTH_PWD}
//             argocd app sync ${ARGOCD_APP} --force
//             argocd app wait ${ARGOCD_APP} --timeout ${APP_WAIT_TIMEOUT}
//             argocd logout ${ArgoURL}
//             """
//           }
//         }
//     }
  }
//   post {
//       success {
//           slackSend (color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
//       }
//       failure {
//           slackSend (color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
//       }
//   }
}

