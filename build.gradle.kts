plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    maven("https://jitpack.io")

    maven("https://repo.networkmanager.xyz/repository/maven-public")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("dev.dejvokep:boosted-yaml:1.3.4")
    compileOnly("nl.chimpgamer.networkmanager:api:2.15.0-SNAPSHOT")
}

group = "nl.chimpgamer.networkmanager.extensions"
version = "1.0.16"
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
        relocate("dev.dejvokep.boostedyaml", "$libPackage.boostedyaml")
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }
}