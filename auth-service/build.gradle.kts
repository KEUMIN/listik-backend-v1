import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.register("prepareKotlinBuildScriptModel")

dependencies {
    // Kotlin dependencies
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":core-service")) {
        // 전이되는 DB 관련 막기
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-data-jpa")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-jdbc")
        exclude(group = "com.zaxxer", module = "HikariCP")
        exclude(group = "org.hibernate.orm", module = "hibernate-core")
        exclude(group = "org.hibernate", module = "hibernate-core")
        exclude(group = "org.flywaydb", module = "flyway-core")
        exclude(group = "org.liquibase", module = "liquibase-core")
    }

    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // OAuth2 dependencies
    implementation("com.google.api-client:google-api-client:2.7.2")
    implementation("com.google.http-client:google-http-client:1.47.0")
    implementation("com.google.http-client:google-http-client-gson:1.47.0")
    implementation("com.nimbusds:nimbus-jose-jwt:9.31")

    // JWT dependencies
    compileOnly("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // FeignClient for inter-service communication
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Swagger/OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // Test dependencies
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}