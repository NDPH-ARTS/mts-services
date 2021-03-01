# This dockerfile includes "conditional layers" and best used with DOCKER_BUILDKIT=1.

ARG DEPS_CACHE=project

FROM maven:3-openjdk-11 as builder

# copy parent poms
COPY ./pom*.xml ./

# copy entire build-tools folder
COPY ./build-tools ./build-tools

# copy poms of shared modules
COPY ./fhir-client/pom.xml ./fhir-client/
COPY ./security/pom.xml ./security/

# copy poms of ALL services
COPY ./config-server/pom.xml ./config-server/
COPY ./discovery-server/pom.xml ./discovery-server/
COPY ./gateway-server/pom.xml ./gateway-server/
COPY ./sample-service/pom.xml ./sample-service/
COPY ./init-service/pom.xml ./init-service/
COPY ./practitioner-service/pom.xml ./practitioner-service/
COPY ./role-service/pom.xml ./role-service/
COPY ./role-service-client/pom.xml ./role-service-client/
COPY ./site-service/pom.xml ./site-service/

FROM builder as deps-cache-project
ARG SVC
# cache project level dependencies as a layer to be used in other builds while POMs don't change
RUN mvn dependency:go-offline --projects ${SVC} --batch-mode -q --also-make -Dmaven.wagon.http.retryHandler.count=3

FROM builder as deps-cache-full
# cache ALL dependencies as a layer to be used in other builds while POMs don't change.
# the layer produced could be used for all projects, good for local running, not ci.
RUN mvn dependency:go-offline --batch-mode -q -Dmaven.wagon.http.retryHandler.count=3

FROM deps-cache-${DEPS_CACHE} as builder-cached
ARG SVC
# It's easier to copy everything due to dependencies between modules.
# dockerignore should keep the build context focused on what's required.
COPY . ./
RUN mvn package --projects ${SVC} --batch-mode -q --also-make -Dmaven.wagon.http.retryHandler.count=3

FROM adoptopenjdk/openjdk11:jre-11.0.10_9-debianslim
ARG SVC
ARG JAR_FILE=${SVC}/target/*.jar
COPY --from=builder-cached ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080:80
