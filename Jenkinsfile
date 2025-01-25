
@Library('jjva-canada-channels') _

pipeline {
  agent { label 'jjva-google-jenkins-slave' }

  environment {
    deploymentName = "devsecops"
    containerName = "devsecops-container"
    serviceName = "devsecops-svc"
    imageName = "siddharth67/numeric-app:${GIT_COMMIT}"
    applicationURL = "http://devsecops-demo.eastus.cloudapp.azure.com"
    applicationURI = "/increment/99"
  }

  stages {
    stage('Testing Slack') {
      steps {
        sh 'exit 0'
      }
    }

  }

  post {
    success {
      script {
        /* Use slackNotifier.groovy from shared library and provide current build result as parameter */
        env.failedStage = "none"
        env.emoji = ":white_check_mark: :tada: :thumbsup_all:"
        sendNotification currentBuild.result
      }
    }

    // failure {

    // }
  }
}
