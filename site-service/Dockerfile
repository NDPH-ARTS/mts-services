FROM maven:3-openjdk-11 as builder
ARG SVC

# copy common things
COPY ./build-tools ./build-tools
COPY ./pom.xml pom.xml

# copy poms of ALL services
COPY ./site-service/pom.xml ./site-service/pom.xml
COPY ./trial-config-service/pom.xml ./trial-config-service/pom.xml
COPY ./practitioner-service/pom.xml ./practitioner-service/pom.xml
COPY ./sample-service/pom.xml ./sample-service/pom.xml
COPY ./role-service/pom.xml ./role-service/pom.xml

# copy current service source
COPY ./${SVC}/src ${SVC}/src

RUN mvn package --projects ${SVC} # -Dmaven.test.failure.ignore=true

FROM scratch AS export
ARG SVC
COPY --from=builder ${SVC}/target/surefire-reports/TEST*.xml ./

FROM openjdk:11.0.9.1-jre-buster
ARG SVC
ARG JAR_FILE=${SVC}/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080:80


