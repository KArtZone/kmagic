plugins {
    kotlin("jvm") version "2.2.21"
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

    testImplementation(libs.kotest.core)
    testImplementation(libs.kotest.assertions)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}