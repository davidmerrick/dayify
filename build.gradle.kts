group = "io.github.davidmerrick.dayify"

repositories {
    mavenLocal()
    mavenCentral()
}

plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm") version "1.8.0"
    kotlin("kapt") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.0"
    id("io.micronaut.application") version "3.6.2"
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("io.github.davidmerrick.dayify.*")
    }
}

application {
    mainClass.set("$group.Application")
}

allOpen {
    annotations(
        "io.micronaut.aop.Around",
        "io.micronaut.http.annotation.Controller",
        "javax.inject.Singleton"
    )
}

java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

dependencies {
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.jaxrs:micronaut-jaxrs-processor")

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut:micronaut-http-client")
    implementation("javax.annotation:javax.annotation-api")
    implementation("io.micronaut:micronaut-validation")

    implementation("net.sf.biweekly:biweekly:0.6.7")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.6.4")

    // Logging

    implementation("io.github.microutils:kotlin-logging:3.0.0")
    runtimeOnly("ch.qos.logback:logback-classic")

    // Test

    kaptTest("io.micronaut:micronaut-inject-java")
    testImplementation("org.spekframework.spek2:spek-runner-junit5:2.0.18")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testImplementation("io.micronaut.test:micronaut-test-spock")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("io.mockk:mockk:1.12.8")
    testImplementation("com.github.tomakehurst:wiremock:2.27.2")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.4.2")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "17" }

    test {
        useJUnitPlatform()
    }

    shadowJar {
        archiveBaseName.set("application")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}
