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

// monolith
module(name = ":monolith-common", path = "monolith/common")
module(name = ":monolith-payment", path = "monolith/payment-module")
module(name = ":monolith-wallet", path = "monolith/wallet-module")
module(name = ":monolith-ledger", path = "monolith/ledger-module")
module(name = ":monolith-application", path = "monolith/application")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

fun module(name: String, path: String) {
    include(name)
    project(name).projectDir = file(path)
}
