//${library.jenkins-slack-library.version}
@Library('jvva-canada-channels-bot-user-slack-connections') _

pipeline {

  agent { label 'jjva-google-jenkins-slave' }

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
    //Apps environments properties
    myApp="mss-java-app"
    sonarName="jjva-mss-java-web-report-app"
    dockerName="jjva-mss-java-web-ig-app"
    nexusName="jjva-mss-java-warfile-app"
    promeName="prometheus-server"
    alertM="prometheus-alertmanager"
    alertName="prometheus-alertmanager"
    graName="grafana"
    //website url properties
    webSite="http://mdb.eagunu4live.com/java-web-app"
    sonarIP="http://34.75.174.149"
    nexusIP="http://34.23.207.210"
    promeLink="http://prome.eagunu4live.com"
    grafanaURL="http://grafana.eagunu4live.com"
    alertURL="http://alert.eagunu4live.com"
    alartLink="http://alert.eagunu4live.com"
    dockerlink="https://hub.docker.com/repository/docker/eagunuworld/jjva-mss-java-web-app"
    //Codes environment properties
    GIT_COMMIT = "${GIT_COMMIT}"
    GIT_BRANCH="${GIT_BRANCH}"
    GIT_PREVIOUS_SUCCESSFUL_COMMIT   = "${GIT_PREVIOUS_SUCCESSFUL_COMMIT}"
    BUILD_NUMBER = "${env.BUILD_ID}"
    //jjva-mss-java-web-app sonarqubetoken
    jjva_java_sonar_token="sqp_db11ea5f16674caeb3bafc7ae4c9d760dd24d042"
    //Sonareqube externalIP Idress
    sonar_IP_address="34.75.174.149"
    //eagunu docker registry repository
    registry = "eagunuworld/jjva-mss-java-web-ig-app"
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
    NEXUS_URL = "34.23.207.210:8081"
    // Repository where we will upload the artifact
    NEXUS_REPOSITORY = "jjva-mss-java-warfile-app"
    // Jenkins credential id to authenticate to Nexus OSS
    NEXUS_CREDENTIAL_ID = "nexus-username-password-creds-demoshool"
    //sonar qube
  }

   stages {
    stage('Cloning Git') {
            steps {
                //checkout([$class: 'GitSCM', branches: [[name: '*/prod-master']], extensions: [], userRemoteConfigs: [[credentialsId: 'democalculus-github-login-creds', url: 'https://github.com/democalculus/kubana-maven-web-app.git']]])
                git credentialsId: 'GIT_CREDENTIALS', url:  'https://github.com/agunuworld4/jjva-mss-java-web-app.git',branch: 'main'
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
      sh "mvn clean clean package sonar:sonar -Dsonar.projectKey=jjva-mss-java-web-app -Dsonar.projectName='jjva-mss-java-web-app' -Dsonar.host.url=http://${sonar_IP_address}:9000 -Dsonar.token=${jjva_java_sonar_token}"
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
                   dockerImage = docker.build registry + "$BUILD_NUMBER"
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

    stage('Cleaning  up docker Images') {
        steps{
           sh 'docker rmi  $(docker images -q)'
           }
         }

 }  //This line end the pipeline stages yes pls 
  post {   //This line start the post script uncommit later
    success {
      script {
        //* Use slackNotifier.groovy from shared library and provide current build result as parameter */
        env.failedStage = "none"
        env.emoji = ":white_check_mark: :tada: :thumbsup_all:"
        sendNotification currentBuild.result
      }
      }

    // failure {
    //}
  }  //this line close post script stage
}    //This line close the jenkins pipeline
