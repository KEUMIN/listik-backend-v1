tasks.register("prepareKotlinBuildScriptModel")

dependencies {
    implementation(project(":auth-service"))
    implementation(project(":user-service"))
    implementation(project(":core-service"))
    implementation(project(":book-service"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ✅ Hibernate에서 사용하는 공통 어노테이션 의존성 명시
    implementation("org.hibernate.common:hibernate-commons-annotations:6.0.6.Final")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
}
