plugins {
    kotlin("jvm") version "1.8.0"
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.apache.flink:flink-streaming-java_2.12:1.14.0")
    implementation("org.apache.flink:flink-clients_2.12:1.14.0")
    implementation("org.apache.flink:flink-connector-kafka_2.12:1.14.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    implementation("org.slf4j:slf4j-simple:1.7.30")
}

application {
    mainClass.set("MainKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<JavaExec> {
    jvmArgs("--add-opens", "java.base/java.util=ALL-UNNAMED")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "MainKt"
        )
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}