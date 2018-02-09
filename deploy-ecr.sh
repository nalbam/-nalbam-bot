#!/bin/bash

nothing() {
    USERID=
    REGION=
}

echo_() {
    echo -e "$1"
}

error() {
    echo_ "$1"
    exit 1
}

REGION=us-east-1

ARTIFACT_ID=nalbam-bot
VERSION=0.0.0

if [ "${USERID}" != "" ]; then
    REPOSITORY="${USERID}.dkr.ecr.${REGION}.amazonaws.com"
fi

if [ "${REPOSITORY}" == "" ]; then
    error "Not set REPOSITORY."
fi

echo_ "publish to docker... [${REPOSITORY}]"

docker version

pushd target/docker

echo_ "docker build... [${ARTIFACT_ID}]"

docker build --rm=false -t ${REPOSITORY}/${ARTIFACT_ID}:${VERSION} .

docker images

echo_ "docker login..."

ECR_LOGIN=$(aws ecr get-login --region ${REGION} --no-include-email)
#${ECR_LOGIN}

echo_ "docker push... [${ARTIFACT_ID}]"

docker push ${REPOSITORY}/${ARTIFACT_ID}:${VERSION}

popd
