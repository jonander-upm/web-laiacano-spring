FROM eclipse-temurin:17-jdk
VOLUME /tmp
ARG JAR_FILE=target/core-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Djava.security.edg=file:/dev/./urandom", "-jar", "/app.jar"]