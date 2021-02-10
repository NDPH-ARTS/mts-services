FROM maven:3-openjdk-11 as builder
ARG SVC

# copy common things
COPY ./build-tools ./build-tools
COPY ./pom*.xml ./

# copy poms of ALL services
COPY ./config-server/pom.xml ./config-server/
COPY ./discovery-server/pom.xml ./discovery-server/
COPY ./sample-service/pom.xml ./sample-service/
COPY ./init-service/pom.xml ./init-service/
COPY ./practitioner-service/pom.xml ./practitioner-service/
COPY ./role-service/pom.xml ./role-service/
COPY ./site-service/pom.xml ./site-service/

# cache most dependencies as a layer to be used in other builds while POMs don't change
RUN mvn dependency:go-offline --batch-mode -q --projects ${SVC}

# copy current service source
COPY ./${SVC}/src ${SVC}/src

RUN mvn package --projects ${SVC} # -Dmaven.test.failure.ignore=true

FROM openjdk:11.0.9.1-jre-buster
ARG SVC
ARG JAR_FILE=${SVC}/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080:80
