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

    // Kotlin standard library
    implementation(kotlin("stdlib"))

    // Testing
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.cardano.lil.MainKt")
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
            imageName.set("lil")
            mainClass.set("org.cardano.lil.MainKt")

            buildArgs.add("--verbose")
            buildArgs.add("--no-fallback")
            buildArgs.add("-H:+ReportExceptionStackTraces")
            buildArgs.add("--initialize-at-build-time=kotlin")

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
