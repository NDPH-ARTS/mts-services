# This dockerfile includes "conditional layers" and best used with DOCKER_BUILDKIT=1.
ARG DEPS_CACHE=project
ARG BUILDER_IMAGE=maven:3-openjdk-11
ARG RUNTIME_IMAGE=adoptopenjdk/openjdk11:jre-11.0.10_9-debianslim
ARG APPINSIGHTS_FILE=applicationinsights-agent-3.0.2.jar
ARG APPINSIGHTS_URL_PREFIX=https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.0.2


FROM ${BUILDER_IMAGE} as builder
ARG APPINSIGHTS_FILE
ARG APPINSIGHTS_URL_PREFIX
# downloading in builder to copy the jar and config file together in 1 layer
RUN wget "${APPINSIGHTS_URL_PREFIX}/${APPINSIGHTS_FILE}" --output-document applicationinsights-agent.jar

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

FROM ${RUNTIME_IMAGE}
ARG SVC
ARG JAR_FILE=${SVC}/target/*.jar
COPY --from=builder-cached applicationinsights* .
COPY --from=builder-cached ${JAR_FILE} app.jar
ENTRYPOINT ["java","-javaagent:applicationinsights-agent.jar", "-jar","/app.jar"]
EXPOSE 8080:80
