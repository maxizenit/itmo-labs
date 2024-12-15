plugins {
    java
    id("io.freefair.lombok") version "8.11"
}

group = "ru.itmo.textanalyzer"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}