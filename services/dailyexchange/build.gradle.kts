plugins {
    java
    id("org.springframework.boot") version "4.0.3"
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

repositories {
    mavenCentral()
}

val mockitoAgent by configurations.creating

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    "mockitoAgent"("org.mockito:mockito-core:5.23.0") {
        isTransitive = false
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    jvmArgs("-javaagent:${configurations.getByName("mockitoAgent").asPath}")

    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
    }
}
