#  Suspect this Dockerfile is now obsolete - build from the root of mts-services with --build-arg SVC=desired service


# ARG SVC

# FROM maven:3-openjdk-11 as builder
# ARG SVC

# #TODO: bring only rquired pom files.
# COPY ./pom.xml ./trial-config-service/pom.xml
# COPY ../practitioner-service/pom.xml ./practitioner-service/pom.xml
# COPY ../sample-service/pom.xml ./sample-service/pom.xml
# COPY ./${SVC}/src ${SVC}/src

# # Copy parent pom
# COPY ../pom.xml pom.xml

# RUN mvn package --projects ${SVC} # -Dmaven.test.failure.ignore=true

# FROM scratch AS export
# ARG SVC
# COPY --from=builder ${SVC}/target/surefire-reports/*.xml test-reports/

# FROM openjdk:11.0.9.1-jre-buster
# ARG SVC
# ARG JAR_FILE=${SVC}/target/*.jar
# COPY --from=builder ${JAR_FILE} app.jar
# ENTRYPOINT ["java","-jar","/app.jar"]
# EXPOSE 8080:80


