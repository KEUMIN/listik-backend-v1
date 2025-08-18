FROM openjdk:17-jre-slim

WORKDIR /app

COPY build/libs/user-service.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]