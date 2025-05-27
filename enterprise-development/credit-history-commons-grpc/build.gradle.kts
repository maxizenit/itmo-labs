import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "8.13.1"
}

group = "ru.itmo.credithistory"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<BootJar> {
    enabled = false
}

repositories {
    mavenCentral()
}

extra["springGrpcVersion"] = "0.8.0"

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")

    implementation("io.grpc:grpc-services")

    compileOnly("org.jspecify:jspecify:1.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.grpc:spring-grpc-test")
    testImplementation("org.assertj:assertj-core:3.27.3")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
    }
}