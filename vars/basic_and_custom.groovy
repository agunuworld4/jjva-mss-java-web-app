def call(String buildStatus = 'STARTED') {
 buildStatus = buildStatus ?: 'SUCCESS'

 def color

 if (buildStatus == 'SUCCESS') {
  color = '#47ec05'
 } else if (buildStatus == 'UNSTABLE') {
  color = '#d5ee0d'
 } else {
  color = '#ec2805'
 }

 def msg = "${buildStatus}: `${env.JOB_NAME}` #${env.BUILD_NUMBER}:\n${env.BUILD_URL}"

 slackSend(color: color, message: msg)
}
===============
[
  "type": "divider"
],
[
  "type": "section",
  "fields": [
    [
      "type": "mrkdwn",
      "text": "*Codes Analysis Name:*\n${sonarName}"
    ],
    [
      "type": "mrkdwn",
      "text": "*Sonar Port*\n9000"
    ]
  ],
  "accessory": [
    "type": "image",
    "image_url": "https://github.com/agunuworld4/slack-devops-emoji/blob/main/main/slack-emojis/java-sonar.png",
    "alt_text": "SonarQube Icon"
  ],
],

[
  "type": "section",
  "text": [
      "type": "mrkdwn",
      "text": "*Analysis logs: * `Scanning`"
    ],
  "accessory": [
    "type": "button",
    "text": [
      "type": "plain_text",
      "text": "SonarQube URL",
      "emoji": true
    ],
    "value": "click_me_123",
    "url": "${sonarIP}:9000",
    "action_id": "button-action"
  ]
],
====================Nexus
[
  "type": "divider"
],
[
  "type": "section",
  "fields": [
    [
      "type": "mrkdwn",
      "text": "*Package Repo Name:*\n${nexusName}"
    ],
    [
      "type": "mrkdwn",
      "text": "*Nexus Port*\n8081"
    ]
  ],
  "accessory": [
    "type": "image",
    "image_url": "https://raw.githubusercontent.com/agunuworld4/slack-devops-emoji/refs/heads/main/main/slack-emojis/nexus.png",
    "alt_text": "Nexus Icon"
  ],
],

[
  "type": "section",
  "text": [
      "type": "mrkdwn",
      "text": "*War File: * `Package`"
    ],
  "accessory": [
    "type": "button",
    "text": [
      "type": "plain_text",
      "text": "Nexus URL",
      "emoji": true
    ],
    "value": "click_me_123",
    "url": "${nexusIP}:8081",
    "action_id": "button-action"
  ]
],
===============
