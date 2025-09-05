# 루트에서 이미지 생성 : docker build -f auth-service/Dockerfile -t local/auth-service:dev .
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew && ./gradlew :auth-service:clean :auth-service:bootJar --no-daemon


FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=builder /workspace/auth-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]