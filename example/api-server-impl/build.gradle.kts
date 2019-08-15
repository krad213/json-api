import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm").version("1.3.41")
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.slf4j:slf4j-api:1.7.28")
    implementation("org.slf4j:slf4j-simple:1.7.28")
    implementation("com.sparkjava:spark-core:2.9.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")

    implementation(project(":api-dto-impl"))
    implementation("su.kore:api-common:1.0")
    implementation("su.kore:api-server:1.0")
}

