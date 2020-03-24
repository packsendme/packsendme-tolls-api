FROM openjdk:8-jdk-alpine
EXPOSE 9099
COPY /target/packsendme-google-api-0.0.1-SNAPSHOT.jar packsendme-google-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/packsendme-google-api-0.0.1-SNAPSHOT.jar"]