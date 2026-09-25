plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(ktorLibs.plugins.ktor)
}

group = "pro.artkart"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.cio.EngineMain"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.server.cio)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.contentNegotiation)

    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.contentNegotiation)

    implementation(ktorLibs.serialization.kotlinx.json)

    implementation(libs.coroutines.core)

    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    implementation(libs.arrow.fx.stm)
    implementation(libs.arrow.resilience)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.postgresql)

    implementation(libs.logging)
    implementation(libs.logback)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.core)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.coroutines.test)
    testImplementation(ktorLibs.server.testHost)
}

tasks.test {
    useJUnitPlatform()
}
