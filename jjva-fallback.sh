#!/bin/bash

mss_pod_app="mss-java-pod"
imageVersion="eagunuworld/jjva-mss-java-web-app:v$BUILD_NUMBER"


sleep 20s

if [[ $(kubectl -n jjva-ns-svc-pod get deploy ${mss_pod_app} --timeout 5s) = *"successfully rolled out"* ]];
then
	echo "Deployment ${mss_pod_app} was successful"
    exit 0;
else
	echo "mss pod Dployment ${mss_pod_app} Is Up And Running"
	echo "Here is the image: ${imageVersion}"
fi
