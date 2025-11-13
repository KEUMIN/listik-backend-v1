import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

dependencies {
    // Kotlin dependencies
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":core-service")) {
        // 서블릿 스택/웹 MVC 제거
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        exclude(group = "org.springdoc", module = "springdoc-openapi-starter-webmvc-ui")

        // JPA/DB 관련 전이 제거
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-data-jpa")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-jdbc")
        exclude(group = "com.zaxxer", module = "HikariCP")
        exclude(group = "org.hibernate.orm", module = "hibernate-core")
        exclude(group = "org.hibernate", module = "hibernate-core")

        // 시큐리티는 게이트웨이에서 WebFlux 기반으로 직접 관리 (중복/충돌 방지)
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-security")
    }

    // Spring Cloud Gateway dependencies
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // JWT validation
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.1")
    }
}