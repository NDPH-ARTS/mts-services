## HOW TO
# docker build -t docker-sibling-creator . -f localenv/Dockerfile
# docker run -it -v "/var/run/docker.sock:/var/run/docker.sock" -e "GITHUB_SHA=thursday" docker-sibling-creator

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
#CMD localenv/local-ci-env.sh
ENV DOCKER_BUILDKIT=1
CMD docker version \
    && echo "build a service in a sibling container - everything before COPY cmd" && docker build --build-arg SVC="role-service" -t "up-to-copy-cmd"  . -f Dockerfile-up-to-copy-cmd \
    && echo "build a service in a sibling container - with COPY cmd" && docker build --build-arg SVC="role-service" -t "with-copy-cmd"  . -f Dockerfile-with-copy-cmd
