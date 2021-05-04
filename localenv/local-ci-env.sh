#!/usr/bin/env bash
# This is a scripted version of the github pipeline docker-build-push.yml build, init and test actions.


build(){
  declare GHCR="ghcr.io/ndph-arts"
  declare -a services=( "practitioner-service" "site-service"  "role-service" "init-service" "config-server" "discovery-server" "gateway-server")  #Equivalent of strategy.matrix.service_name in docker-build-push.yml workflow.   
  for service in "${services[@]}"
  do
    tag=$GHCR/$service:$GITHUB_SHA
    echo "Build $service $tag"
    docker build --build-arg SVC="$service" -t "$tag"  .
  done
}

start_services(){
    export PROFILE="dev"
    #declare dbpassword="$(openssl rand -base64 12)" #No we want to fix this so we can plug in services
    export SAPASSWORD="$SAPASSWORD"
    export GITHUB_SHA="$GITHUB_SHA"
    export MTS_AZURE_APP_CLIENT_ID="fa5cde1d-d6f8-4d13-9fa4-4d7a374cb290"
    export INIT_AZURE_CLIENT_ID="14fa4ff6-9396-40aa-afdf-09eb1f4e6292"
    export INIT_AZURE_CLIENT_SECRET="$INIT_SERVICE_SECRET"
    export INIT_AZURE_TENANT_ID="99804659-431f-48fa-84c1-65c9609de05b"
    export LOGGING_LEVEL_ROOT="INFO"
    echo "Start services"
    .ci/docker/check-docker-compose-services.sh
}

run_init(){
    echo "Run init"
    docker-compose run --no-deps init-service
}

run_api_tests(){

  export BASE_URL="http://host.docker.internal:8080"
  export MTS_AZURE_UI_APP_CLIENT_ID="59a2b30f-844b-4a69-b034-19e3d2d4d805"
  export MTS_AUTHORISATION_BACKEND_SCOPE="api://fa5cde1d-d6f8-4d13-9fa4-4d7a374cb290/default"
  export AUTOMATION_USER_NAME="test-automation@mtsdevndph.onmicrosoft.com"
  export AUTOMATION_USER_PASSWORD="$AUTOMATION_USER_PASSWORD"
  export BOOTSTRAP_USER_NAME="bootstrap@mtsdevndph.onmicrosoft.com"
  export BOOTSTRAP_USER_PASSWORD="$BOOTSTRAP_USER_PASSWORD"
  export QAWITHCREATE_USER_NAME="qa.with-create@mtsdevndph.onmicrosoft.com"
  export QAWITHCREATE_USER_PASSWORD="$QA_WITH_CREATE_USER_PASSWORD"
  echo "Run API Tests against $BASE_URL"
  npm run --prefix api-tests api-test-ci-local

}

test(){
  start_services
  run_init
  run_api_tests
}
checkEnvVarIsSet() {
  if [ -z `printenv $1` ];
  then
    echo "$1 is not set"
    exit
  fi
}
checkAllEnv(){
  checkEnvVarIsSet GITHUB_SHA
  checkEnvVarIsSet SAPASSWORD
  checkEnvVarIsSet INIT_SERVICE_SECRET
  checkEnvVarIsSet AUTOMATION_USER_PASSWORD
  checkEnvVarIsSet BOOTSTRAP_USER_PASSWORD
  checkEnvVarIsSet QA_WITH_CREATE_USER_PASSWORD
}

checkAllEnv # Fail early
build
test









