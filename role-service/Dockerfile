FROM maven:3-openjdk-11 as builder
ARG SVC

# copy parent poms
COPY ./pom*.xml ./

# copy entire build-tools folder
COPY ./build-tools ./build-tools
COPY ./security ./security

# copy poms of shared modules
COPY ./fhir-client/pom.xml ./fhir-client/

# copy poms of ALL services
COPY ./config-server/pom.xml ./config-server/
COPY ./discovery-server/pom.xml ./discovery-server/
COPY ./gateway-server/pom.xml ./gateway-server/
COPY ./sample-service/pom.xml ./sample-service/
COPY ./init-service/pom.xml ./init-service/
COPY ./practitioner-service/pom.xml ./practitioner-service/
COPY ./role-service/pom.xml ./role-service/
COPY ./site-service/pom.xml ./site-service/

# cache most dependencies as a layer to be used in other builds while POMs don't change
RUN mvn dependency:go-offline --batch-mode -q -Dmaven.wagon.http.retryHandler.count=3 --projects ${SVC}

# copy current service source
COPY ./${SVC}/src ${SVC}/src

RUN mvn package --batch-mode -q --projects ${SVC}

FROM adoptopenjdk/openjdk11:jre-11.0.10_9-debianslim
ARG SVC
ARG JAR_FILE=${SVC}/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080:80
