FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew && ./gradlew :gateway-service:clean :gateway-service:bootJar --no-daemon


FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=builder /workspace/gateway-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]