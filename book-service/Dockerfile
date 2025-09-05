FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew && ./gradlew :book-service:clean :book-service:bootJar --no-daemon


FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=builder /workspace/book-service/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]