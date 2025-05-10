tasks.register("prepareKotlinBuildScriptModel")

dependencies {
    implementation(project(":core-service"))
    implementation(project(":user-service"))

    // Spring Security + OAuth2
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // 1) Core Google API Client (여기에 GoogleIdToken, GoogleIdTokenVerifier 등 포함)
    implementation("com.google.api-client:google-api-client:2.7.2")
    // 2) HTTP 전송을 위한 NetHttpTransport
    implementation("com.google.http-client:google-http-client:1.47.0")
    // 3) JSON 파싱용 GsonFactory
    implementation("com.google.http-client:google-http-client-gson:1.47.0")

    // jwt
    compileOnly("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    testImplementation("org.springframework.security:spring-security-test")
}
