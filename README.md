# A base repo for all microservices

### Running locally using docker compose
#### Option 1 - All services
```sh
export PROFILE=dev
docker-compose up -d
```

#### Option 2 - All services in local mode, without service discovery nor config
```sh
export PROFILE=local
docker-compose up -d
```