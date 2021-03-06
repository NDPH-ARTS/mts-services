#!/usr/bin/env bash
#
# This is an lite integration script that brings the services up, and init the system.

set -o errexit
set -o pipefail
set -o nounset
# set -o xtrace

is_healthy() {
    service="$1"
    container_id="$(docker-compose ps -q "$service")"
    health_status="$(docker inspect -f "{{.State.Health.Status}}" "$container_id")"

    if [ "$health_status" = "healthy" ]; then
        echo "$1 is healthy"
        return 0
    else
        echo "Waiting for $1, logs tail is:"
        docker-compose logs --tail=10 ${service}
        return 1
    fi
}
# on CI we want to test ALL services (discovery, config and gateway) using dev profile (instead of the local default)
docker-compose up --no-build -d discovery-server config-server gateway-server

# start it here to save some time
docker-compose up --no-build -d fhir-api

echo "Waiting for services to become healthy..."
while ! is_healthy discovery-server; do sleep 10; done
while ! is_healthy config-server; do sleep 10; done
while ! is_healthy gateway-server; do sleep 10; done

docker-compose up --no-build -d practitioner-service site-service role-service

sleep 10

echo "Services started."
