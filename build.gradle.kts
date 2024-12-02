plugins {
    kotlin("jvm") version "2.0.20"
    id("dev.vexide.hydrozoa") version "0.1.0-alpha.1"
    idea
}

hydrozoa {
    entrypoint = "org.example.MainKt"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.vexide:hydrozoa:0.1.0-alpha.2")
}

kotlin {
    jvmToolchain(21)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}