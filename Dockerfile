# This dockerfile includes "conditional layers" and best used with DOCKER_BUILDKIT=1.
ARG DEPS_CACHE=project
ARG BUILDER_IMAGE=maven:3-openjdk-11-slim
ARG RUNTIME_IMAGE=adoptopenjdk/openjdk11:jre-11.0.10_9-debianslim
ARG APPINSIGHTS_FILE=applicationinsights-agent-3.0.2.jar
ARG APPINSIGHTS_URL_PREFIX=https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.0.2

FROM ${BUILDER_IMAGE} as builder
ARG APPINSIGHTS_FILE
ARG APPINSIGHTS_URL_PREFIX
# downloading first since it isn't updated too much (hence cached)
RUN curl -L "${APPINSIGHTS_URL_PREFIX}/${APPINSIGHTS_FILE}" --output applicationinsights-agent.jar

# This section copies the requirements to build our project, but also considers layer caching.
# Start by copying things that are less likely to be updated and make our cache last longer.
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
COPY ./site-service-client/pom.xml ./site-service-client/

FROM builder as deps-cache-project
ARG SVC
# cache project level dependencies as a layer to be used in other builds while POMs don't change
RUN mvn dependency:go-offline --projects ${SVC} --batch-mode -q --also-make -Dmaven.wagon.http.retryHandler.count=3

FROM builder as deps-cache-full
# Cache ALL dependencies as a layer to be used in other builds while POMs don't change.
# This stage can be used for all projects - used in the CI, local is also possible.
RUN mvn dependency:go-offline --batch-mode -q -Dmaven.wagon.http.retryHandler.count=3

FROM deps-cache-${DEPS_CACHE} as builder-cached
ARG SVC
ARG VERSION=dev
# It's easier to copy everything due to dependencies between modules.
# dockerignore should keep the build context focused on what's required.
COPY . ./
RUN mvn package --projects ${SVC} --batch-mode -q --also-make -Dmaven.wagon.http.retryHandler.count=3
# inject the version into application insights settings.
# jq was preferred but the builder image doesn't have it. Instead of installing it, using sed...
RUN sed "s/unknown_version/${VERSION}/" applicationinsights.json > applicationinsights.runtime.json

# This stage allows us to prepare all the files we need for runtime and copy in 1 layer
FROM scratch as pre-runtime
ARG SVC
ARG JAR_FILE=${SVC}/target/*.jar
COPY --from=builder-cached applicationinsights*.jar ./runtime/
COPY --from=builder-cached applicationinsights.runtime.json ./runtime/applicationinsights.json
COPY --from=builder-cached ${JAR_FILE} ./runtime/app.jar

FROM ${RUNTIME_IMAGE}
COPY --from=pre-runtime ./runtime/* ./
ENTRYPOINT ["java","-javaagent:applicationinsights-agent.jar", "-jar","/app.jar"]
EXPOSE 8080:80
