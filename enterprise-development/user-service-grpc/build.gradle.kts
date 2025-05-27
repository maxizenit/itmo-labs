import com.google.protobuf.gradle.id
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
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
    compileOnly("org.springframework.grpc:spring-grpc-spring-boot-starter")

    compileOnly("io.grpc:grpc-services")

    compileOnly("org.jspecify:jspecify:1.0.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    option("jakarta_omit")
                    option("@generated=omit")
                }
            }
        }
    }
}