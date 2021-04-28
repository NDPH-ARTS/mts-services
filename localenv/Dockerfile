## HOW TO
# docker build -t docker-bag-alpine . -f localenv/Dockerfile
# docker run -it -v "/var/run/docker.sock:/var/run/docker.sock" -e "GITHUB_SHA=foo" docker-bag-alpine

FROM alpine
RUN apk update
RUN apk add bash
# NB this is just for Docker CLI, also need to bind mount the docker socket docker run -v /var/run/docker.sock:/var/run/docker.sock see https://jpetazzo.github.io/2015/09/03/do-not-use-docker-in-docker-for-ci/ and https://tomgregory.com/running-docker-in-docker-on-windows/
RUN apk add docker
RUN apk add docker-compose
#install npm
RUN apk add npm
#copy everything into the container
COPY . /
CMD localenv/local-ci-env.sh
