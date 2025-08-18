pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "listik-backend"
include("api-service", "user-service", "core-service", "book-service")