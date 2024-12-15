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

dependencies {
    implementation("commons-io:commons-io:2.18.0")
    implementation("commons-codec:commons-codec:1.17.1")
    implementation("org.noggit:noggit:0.8")
}