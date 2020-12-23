FROM openjdk:11.0.9.1-jdk-buster as builder
COPY ./pom.xml pom.xml
COPY ./src src
COPY mvnw .
COPY .mvn .mvn
#RUN mvn package -DskipTests
RUN ./mvnw package -Dmaven.test.failure.ignore=true

FROM scratch AS export
COPY --from=builder target/surefire-reports/TEST*.xml ./

FROM openjdk:11.0.9.1-jre-buster
ARG JAR_FILE=target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080:80


