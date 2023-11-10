#!/bin/bash

IMAGE_NAME="bicyclerentalsystem-telegram-bot"
TAG="dev"
DOCKERHUB_NAME="baffepok"

FULL_NAME="${DOCKERHUB_NAME}/${IMAGE_NAME}:${TAG}"

docker build -t "${FULL_NAME}" .
docker push "${FULL_NAME}"