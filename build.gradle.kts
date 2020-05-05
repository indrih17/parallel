import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven("http://dl.bintray.com/kyonifer/maven")
        maven("https://plugins.gradle.org/m2/")
    }
}

plugins {
    kotlin("jvm") version "1.3.71"
}

repositories {
    mavenCentral()
    jcenter()
    maven("http://dl.bintray.com/kyonifer/maven")
    maven("https://dl.bintray.com/mipt-npm/scientifik")
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation(files("libs/mpj/lib/mpj.jar", "libs/mpj/lib/mpi.jar"))
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.5")

    val komaVersion = "0.12"
    implementation("com.kyonifer", "koma-core-api-common", komaVersion)
    implementation("com.kyonifer", "koma-core-ejml", komaVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}