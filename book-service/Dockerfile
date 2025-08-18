FROM openjdk:17-jre-slim

WORKDIR /app

COPY build/libs/book-service.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]