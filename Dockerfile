FROM openjdk:8-jdk-alpine
EXPOSE 9099
COPY /target/pcks-google-api-0.0.1-SNAPSHOT.jar pcks-google-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/pcks-google-api-0.0.1-SNAPSHOT.jar"]