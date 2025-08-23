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

// Disable bootJar for library module
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

// Enable plain jar
tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier = ""
}

dependencies {
    // Kotlin dependencies
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Spring dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Swagger/OpenAPI for shared configuration
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}