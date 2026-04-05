plugins {
    java
    jacoco
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.dennisromano"
version = "0.0.1-SNAPSHOT"
description = "dailyexchange"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

springBoot {
    mainClass.set("org.dennisromano.dailyexchange.infrastructure.DailyExchangeSpringBootApplication")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    finalizedBy(tasks.jacocoTestReport)

    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}