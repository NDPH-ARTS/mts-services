#!/usr/bin/env bash

# This is a scripted version of the github pipeline docker-build-push.yml build, init and test actions.
# It is for running the CI environment locally i.e. without github


declare GHCR="ghcr.io/ndph-arts"
declare -a services=( "practitioner-service" "site-service"  "role-service" "init-service" "config-server" "discovery-server" "gateway-server")  #Equivalent of strategy.matrix.service_name in docker-build-push.yml workflow


build(){
  for service in "${services[@]}"
  do
    tag=$GHCR/$service:$GITHUB_SHA
    echo "Build $service $tag"
    #docker build --build-arg SVC="$service" -t "$tag" .
  done
}

test(){

  export PROFILE="dev"

  echo "Start services"
  .ci/docker/check-docker-compose-services.sh

  echo "Run init"
  docker-compose run --no-deps init-service



}

build
test






