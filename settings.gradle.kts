rootProject.name = "payment-system"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.6.0"
}

// reference
module(name = ":payment-service", path = "reference/payment-service")
module(name = ":ledger-service", path = "reference/ledger-service")
module(name = ":wallet-service", path = "reference/wallet-service")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

fun module(name: String, path: String) {
    include(name)
    project(name).projectDir = file(path)
}
