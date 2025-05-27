plugins {
    java
    id("io.freefair.lombok") version "8.13.1"
}

group = "ru.itmo.credithistory"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.validation:jakarta.validation-api:3.1.1")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    compileOnly("org.springdoc:springdoc-openapi-starter-common:2.8.6")
}