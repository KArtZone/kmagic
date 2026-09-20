plugins {
    kotlin("jvm") version "2.4.20"
    id("io.kotest") version "6.2.3"
}

group = "pro.artkart"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    implementation(libs.coroutines.core)
    implementation(libs.logging)
    implementation(libs.logback)

    testImplementation(libs.kotest.core)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.coroutines.test)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}