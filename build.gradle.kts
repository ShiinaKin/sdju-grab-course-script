plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val jacksonVersion: String by project
val okhttpVersion: String by project

group = "io.sakurasou"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("commons-io:commons-io:2.16.1")

    implementation("io.github.oshai:kotlin-logging:7.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    testImplementation("org.slf4j:slf4j-simple:2.0.13")

    testImplementation(kotlin("test"))
}

tasks.shadowJar {
    manifest { attributes["Main-Class"] = "io.sakurasou.MainKt" }
    archiveFileName.set("sdju-grab-course-script-$version.jar")
    from(projectDir) {
        include("LICENCE")
    }
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.jar {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
