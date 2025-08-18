pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "listik-backend"
include("api-service", "auth-service", "user-service", "core-service", "book-service")