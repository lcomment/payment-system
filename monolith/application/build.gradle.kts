plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":monolith-common"))
    implementation(project(":monolith-payment"))
    implementation(project(":monolith-wallet"))
    implementation(project(":monolith-ledger"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // AWS SQS
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs:3.0.3")

    // Micrometer
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:mysql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}

tasks.bootJar {
    enabled = true
    mainClass.set("com.example.payment.monolith.MonolithApplicationKt")
}

tasks.jar {
    enabled = false
}
