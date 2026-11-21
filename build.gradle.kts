plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
}

group = "com.github.korurg"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.material3)
    implementation(libs.koin.core)
    implementation(libs.jimmer.sql.kotlin)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.hikari)
    implementation(libs.sqlite.jdbc)
    implementation(libs.liquibase.core)
    ksp(libs.jimmer.ksp)
    kspTest(libs.jimmer.ksp)
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(26)
}

compose.desktop {
    application {
        mainClass = "com.github.korurg.Main"
    }
}

tasks.test {
    useJUnitPlatform()
}
