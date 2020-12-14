plugins {
    base
    kotlin("jvm") version "1.4.10"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    maven("https://jitpack.io")

    maven("https://repo.maven.apache.org/maven2")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("net.md-5:bungeecord-api:1.14-SNAPSHOT")
    compileOnly("com.github.Carleslc:Simple-YAML:1.4.1")
    compileOnly(files("/libs/NetworkManagerAPI-v2.9.0-SNAPSHOT.jar"))
}

group = "nl.chimpgamer.networkmanager.extensions"
version = "1.0.9"
description = "AutoBan"

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        val tokens = mapOf("version" to project.version)
        from(sourceSets["main"].resources.srcDirs) {
            filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokens)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}-v${project.version}.jar")
        relocate("kotlin", "nl.chimpgamer.networkmanager.lib.kotlin")
        relocate("org.simpleyaml", "nl.chimpgamer.networkmanager.lib.simpleyaml")
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}