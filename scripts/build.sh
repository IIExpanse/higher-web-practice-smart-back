#!/bin/sh

set -eu
mvn clean package
if docker stop smart-back > /dev/null 2>&1; then echo "Stopped container"; fi
if docker rm smart-back > /dev/null 2>&1; then echo "Removed container"; fi
if docker rmi smart-back > /dev/null 2>&1; then echo "Removed old image"; fi
docker build -t smart-back -f ./docker/Dockerfile .