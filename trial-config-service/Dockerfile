FROM maven:3-jdk-8-openj9 AS build
COPY ./pom.xml pom.xml
COPY ./src src
RUN mvn package -B --no-transfer-progress -Dmaven.test.failure.ignore=true

FROM scratch AS export
COPY --from=build target/surefire-reports/*.xml test-reports/

FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY --from=build ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080:80


