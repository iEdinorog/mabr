#!/bin/bash

echo "Remove image"

docker rmi iedinorog/mabr-user-service
docker rmi iedinorog/mabr-api-gateway
docker rmi iedinorog/mabr-authentication-service
docker rmi iedinorog/mabr-discovery-service:latest
docker rmi iedinorog/mabr-file-storage-service
docker rmi iedinorog/mabr-messenger-service
docker rmi iedinorog/mabr-notification-service
