import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("jvm") version "1.3.72"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    mavenLocal()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }

    maven { url = uri("https://jitpack.io") }

    maven { url = uri("https://repo.maven.apache.org/maven2") }
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("net.md-5:bungeecord-api:1.14-SNAPSHOT")
    compileOnly("com.github.Carleslc:Simple-YAML:1.4.1")
    compileOnly(files("/libs/NetworkManagerAPI-v2.8.7.jar"))
}

group = "nl.chimpgamer.networkmanager.extensions"
version = "1.0.7"
description = "AutoBan"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.processResources {
    val tokens = mapOf("version" to project.version)
    from(sourceSets["main"].resources.srcDirs) {
        filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokens)
    }
}

tasks.shadowJar {
    archiveFileName.set("${project.name}-v${project.version}.jar")
    relocate("kotlin", "nl.chimpgamer.networkmanager.lib.kotlin")
    relocate("org.simpleyaml", "nl.chimpgamer.networkmanager.lib.simpleyaml")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}