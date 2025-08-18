tasks.register("prepareKotlinBuildScriptModel")

dependencies {
    implementation(project(":core-service"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ✅ Hibernate에서 사용하는 공통 어노테이션 의존성 명시
    implementation("org.hibernate.common:hibernate-commons-annotations:6.0.6.Final")

    // Spring Security + OAuth2 (moved from auth-service)
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // Google OAuth2 dependencies
    implementation("com.google.api-client:google-api-client:2.7.2")
    implementation("com.google.http-client:google-http-client:1.47.0")
    implementation("com.google.http-client:google-http-client-gson:1.47.0")

    // Apple OAuth2 dependencies
    implementation("com.nimbusds:nimbus-jose-jwt:9.31")

    // JWT dependencies
    compileOnly("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.security:spring-security-test")
}
