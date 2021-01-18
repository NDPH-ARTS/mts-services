export SAPASSWORD=$(openssl rand -base64 12)

docker-compose up -d
echo "Waiting for docker compose services..."
