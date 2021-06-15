FROM alpine
RUN apk update
RUN apk add bash
RUN apk add curl
# NB this is just for Docker CLI, also need to bind mount the docker socket docker run -v /var/run/docker.sock:/var/run/docker.sock see https://jpetazzo.github.io/2015/09/03/do-not-use-docker-in-docker-for-ci/ and https://tomgregory.com/running-docker-in-docker-on-windows/
RUN apk add docker
RUN apk add docker-compose
RUN apk add npm
RUN npm install -g mocha
# copy all source into a folder in the container (the sibling build will copy it from here)
COPY . /mts-services
RUN npm update --prefix mts-services/api-tests
CMD cd /mts-services && chmod +x localenv/local-ci-env.sh && localenv/local-ci-env.sh

