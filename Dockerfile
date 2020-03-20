
FROM openjdk:8-jdk-alpine
EXPOSE 9099
COPY /target/packsendme-tolls-api-server-0.0.1-SNAPSHOT.jar packsendme-tolls-api-server-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/packsendme-tolls-api-server-0.0.1-SNAPSHOT.jar"]

