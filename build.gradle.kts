plugins {
    id("org.springframework.boot") version "3.2.12"
    id("io.spring.dependency-management") version "1.1.4"

    java
    checkstyle
}

java.sourceCompatibility = JavaVersion.VERSION_17

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
}

allprojects {
    group = "com.example"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

    tasks.test {
        useJUnitPlatform()
    }

}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
