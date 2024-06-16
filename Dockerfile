FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /app
COPY . /app/
RUN mvn clean package

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "/app.jar"]