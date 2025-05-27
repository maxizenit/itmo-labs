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
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.18.3")

    compileOnly("org.jspecify:jspecify:1.0.0")
}