FROM maven:3-openjdk-11 as builder
COPY ./sample-service/pom.xml sample-service/pom.xml
COPY ./sample-service/src sample-service/src
COPY ./pom.xml pom.xml
RUN mvn package --projects sample-service # -Dmaven.test.failure.ignore=true

FROM scratch AS export
COPY --from=builder sample-service/target/surefire-reports/TEST*.xml ./

FROM openjdk:11.0.9.1-jre-buster
ARG JAR_FILE=sample-service/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080:80


