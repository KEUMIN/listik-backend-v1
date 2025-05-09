tasks.register("prepareKotlinBuildScriptModel")

dependencies {
    implementation(project(":core-service"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ✅ Hibernate에서 사용하는 공통 어노테이션 의존성 명시
    implementation("org.hibernate.common:hibernate-commons-annotations:6.0.6.Final")

    runtimeOnly("com.h2database:h2") // 또는 MySQL/PostgreSQL
}
