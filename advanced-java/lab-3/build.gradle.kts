plugins {
    id("java")
    id("io.freefair.lombok") version "8.11"
}

group = "com.vk.itmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-core:1.5.12")
    implementation("ch.qos.logback:logback-classic:1.5.12")
}