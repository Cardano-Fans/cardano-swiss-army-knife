plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("kapt") version "2.2.0"
    id("org.graalvm.buildtools.native") version "0.10.4"
    application
}

group = "org.cardano"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

dependencies {
    // PicoCLI
    implementation("info.picocli:picocli:4.7.7")
    kapt("info.picocli:picocli-codegen:4.7.7")

    // Cardano Client Library (full module with all submodules)
    implementation("com.bloxbean.cardano:cardano-client-lib:0.7.0")

    // CIP-30 Data Signature Parser
    implementation("org.cardanofoundation:cip30-data-signature-parser:0.0.12")

    // Cardano Conversions Library (epoch, slot, time conversions)
    implementation("org.cardanofoundation:cf-cardano-conversions-java:1.2.0")

    // SLF4J logging
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.slf4j:slf4j-simple:2.0.16")

    // Kotlin standard library
    implementation(kotlin("stdlib"))

    // Testing
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.cardano.csak.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
    }
}

// Ensure Java compilation also targets JVM 24
tasks.withType<JavaCompile> {
    targetCompatibility = "24"
    sourceCompatibility = "24"
}

kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
    }

    // Configure kapt to use the same JVM target
    correctErrorTypes = true
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("csak")
            mainClass.set("org.cardano.csak.MainKt")

            buildArgs.add("--verbose")
            buildArgs.add("--no-fallback")
            buildArgs.add("-H:+ReportExceptionStackTraces")
            buildArgs.add("--initialize-at-build-time=kotlin")

            // Include resources from dependencies (genesis files)
            buildArgs.add("-H:IncludeResources=genesis-files/.*\\.json")

            // Enable optimization
            buildArgs.add("-O3")
        }
    }

    // Enable agent for metadata collection during testing
    agent {
        defaultMode.set("standard")
    }
}

tasks.withType<JavaExec> {
    // Enable assertions for development
    jvmArgs("-ea")
}
