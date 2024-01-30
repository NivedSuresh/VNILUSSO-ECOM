FROM openjdk:18
ARG JAR_FILE=target/*.jar
COPY target/ADMIN-0.0.1-SNAPSHOT.jar admin.jar
ENTRYPOINT ["java", "-jar", "/admin.jar"]