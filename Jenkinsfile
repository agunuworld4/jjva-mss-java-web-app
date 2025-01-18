//${library.jenkins-slack-library.version}
//@Library('Slack-us-east-jenkins-prod') _

pipeline {

  agent { label 'gcloud-java-slave-node' }

  options {
       buildDiscarder logRotator(
           artifactDaysToKeepStr: '5',
           artifactNumToKeepStr: '5',
           daysToKeepStr: '5',
           numToKeepStr: '5')
          timestamps()
        }

  tools {
      maven 'UI_Maven3..9.9'
  }

  environment {
    BUILD_NUMBER = "${env.BUILD_ID}"
    //eagunu docker registry repository
    registry = "eagunuworld/jjva-mss-java-web-app"
    //eagunu dockerhub registry
    registryCredential = 'eagunuworld-docker-username-and-pwd'
    dockerImage = ''
    //latest_version_update
    imageVersion = "eagunuworld/jjva-mss-java-web-app:v$BUILD_NUMBER"
    // This can be nexus3 or nexus2
    NEXUS_VERSION = "nexus3"
    // This can be http or https
    NEXUS_PROTOCOL = "http"
    // Where your Nexus is running
    NEXUS_URL = "35.226.17.212:8081"
    // Repository where we will upload the artifact
    NEXUS_REPOSITORY = "jjva-mss-java-web-app"
    // Jenkins credential id to authenticate to Nexus OSS
    NEXUS_CREDENTIAL_ID = "nexus-username-password-creds-demoshool"
    //sonar qube
  }

  stages {
    stage('Cloning Git') {
            steps {
                //checkout([$class: 'GitSCM', branches: [[name: '*/prod-master']], extensions: [], userRemoteConfigs: [[credentialsId: 'democalculus-github-login-creds', url: 'https://github.com/democalculus/kubana-maven-web-app.git']]])
                git credentialsId: 'GIT_CREDENTIALS', url:  'https://github.com/agunuworld4/jjva-mss-java-web-app.git',branch: 'lab-master-branch'
            }
        }

    stage ('Build wep app war file') {
      steps {
      sh 'mvn clean package'
       }
    }
   //hardcoded in pom.xml file
    // stage ('SonarQubeReport') {
    //   steps {
    //   sh 'mvn clean package sonar:sonar'
    // }
    //  }

    stage ('SonarQubeReports') {
      steps {
      //sh 'mvn clean package sonar:sonar'
      sh "mvn clean verify sonar:sonar \
          -Dsonar.projectKey=jjva-mss-java-web-app \
           -Dsonar.projectName='jjva-mss-java-web-app' \
            -Dsonar.host.url=http://34.138.102.99:9000 \
             -Dsonar.token=sqp_db11ea5f16674caeb3bafc7ae4c9d760dd24d042"
         }
     }

  // stage ('SonarQube Plugin Report') {
  //      steps {
  //        withSonarQubeEnv('SonarQubeAccessToken') {
  //        //sh "mvn clean package sonar:sonar"
  //        sh "mvn clean package sonar:sonar"
  //         }
  //       }
  //     }

      stage("publish to nexus") {
          steps {
              script {
                  // Read POM xml file using 'readMavenPom' step , this step 'readMavenPom' is included in: https://plugins.jenkins.io/pipeline-utility-steps
                  pom = readMavenPom file: "pom.xml";
                  // Find built artifact under target folder
                  filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                  // Print some info from the artifact found
                  echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                  // Extract the path from the File found
                  artifactPath = filesByGlob[0].path;
                  // Assign to a boolean response verifying If the artifact name exists
                  artifactExists = fileExists artifactPath;

                  if(artifactExists) {
                      echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";

                      nexusArtifactUploader(
                          nexusVersion: NEXUS_VERSION,
                          protocol: NEXUS_PROTOCOL,
                          nexusUrl: NEXUS_URL,
                          groupId: pom.groupId,
                          version: BUILD_NUMBER,
                          repository: NEXUS_REPOSITORY,
                          credentialsId: NEXUS_CREDENTIAL_ID,
                          artifacts: [
                              // Artifact generated such as .jar, .ear and .war files.
                              [artifactId: pom.artifactId,
                              classifier: '',
                              file: artifactPath,
                              type: pom.packaging]
                          ]
                      );

                  } else {
                      error "*** File: ${artifactPath}, could not be found";
                  }
              }
             }
         }

      stage('Building our image') {
           steps{
                script {
                   dockerImage = docker.build registry + ":v$BUILD_NUMBER"
                  }
              }
           }

      // stage('QA approve') {
      //        steps {
      //          notifySlack("Do you approve QA deployment? $registry/job/$BUILD_NUMBER", [])
      //            input 'Do you approve QA deployment?'
      //            }
      //        }

      stage('Deploy our image') {
         steps{
             script {
                docker.withRegistry( '', registryCredential ) {
               dockerImage.push()
              }
            }
           }
         }

    stage('updating image version') {
          steps {
                sh "bash jjva-latest-version-update.sh"
                }
            }

    stage('Cleaning  up docker Images') {
        steps{
           sh 'docker rmi  ${imageVersion}'
           }
         }

  // stage('kubernetes version 2') {
  //              steps {
  //               withKubeConfig([credentialsId: 'us-east-2-prod-eksdemo']) {
  //                  sh "bash maven_web_app_execute.sh"
  //                }
  //              }
  //            }


    stage('JJva-Maven Deployment') {
            steps {
              parallel(
                "Deployment": {
                     sh 'bash jjva-deployment-script.sh'
                    },
                    "Rollout Status": {
                      sh 'bash jjva-fallback.sh'
                        }
                      )
                    }
                }

  // stage ('Deploying To EKS') {
  //      steps {
  //      sh 'kubectl apply -f mss-us-east-2-prod.yml'
  //      }
  //     }

 }  //This line end the pipeline stages
  ///post {   //This line start the post script uncommit later
       // always { umcommit later
          //junit 'target/surefire-reports/*.xml'
         // jacoco execPattern: 'target/jacoco.exec'
        // pitmutation mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
         //dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
         //publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'owasp-zap-report', reportFiles: 'zap_report.html', reportName: 'OWASP ZAP HTML Report', reportTitles: 'OWASP ZAP HTML Report'])

         //Use sendNotifications.groovy from shared library and provide current build result as parameter
        // sendNotification currentBuild.result uncommit later
        //} uncommmit lastr

    //success {
      //script {
        /* Use slackNotifier.groovy from shared library and provide current build result as parameter */
        //env.failedStage = "none"
        //env.emoji = ":white_check_mark: :tada: :thumbsup_all:"
        //sendNotification currentBuild.result
      //}
      //}

    // failure {
    //}
  //}  //this line close post script stage uncommit lasster
}    //This line close the jenkins pipeline
