docker-compose up -d
echo "Waiting for docker compose services..."

is_healthy() {
    service="$1"
    container_id="$(docker-compose ps -q "$service")"
    health_status="$(docker inspect -f "{{.State.Health.Status}}" "$container_id")"

    if [ "$health_status" = "healthy" ]; then
        echo "$1 is healthy"
        return 0
    else
        echo "Waiting for $1"
        return 1
    fi
}

while ! is_healthy practitioner-service; do sleep 5; done