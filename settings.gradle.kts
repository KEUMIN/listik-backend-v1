pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "oauth"
include("api-service", "auth-service", "user-service", "core-service")