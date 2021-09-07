plugins {
    kotlin("jvm") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    maven("https://jitpack.io")

    maven("https://repo.networkmanager.xyz/repository/maven-public")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
    compileOnly("com.github.Carleslc:Simple-YAML:1.7.2")
    compileOnly("nl.chimpgamer.networkmanager:api:2.10.0")
}

group = "nl.chimpgamer.networkmanager.extensions"
version = "1.0.11"
description = "AutoBan"

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        expand("version" to project.version)
    }

    shadowJar {
        archiveFileName.set("${project.name}-v${project.version}.jar")

        val shadedPackage = "nl.chimpgamer.networkmanager.shaded"
        val libPackage = "nl.chimpgamer.networkmanager.lib"

        //relocate("net.kyori", "$shadedPackage.kyori")
        relocate("kotlin", "$libPackage.kotlin")
        relocate("org.simpleyaml", "$libPackage.simpleyaml")
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }
}