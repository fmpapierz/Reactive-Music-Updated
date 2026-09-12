pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
        maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
        gradlePluginPortal()
        mavenCentral()
    }

    // Declared centrally so each loader module can apply its plugin without repeating a version.
    val fabricLoomVersion = extra["fabric_loom_version"] as String
    val moddevgradleVersion = extra["moddevgradle_version"] as String
    val forgegradleVersion = extra["forgegradle_version"] as String

    plugins {
        id("net.fabricmc.fabric-loom") version fabricLoomVersion
        id("net.neoforged.moddev") version moddevgradleVersion
        id("net.minecraftforge.gradle") version forgegradleVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "reactive-music"

include("common")
include("fabric")
include("quilt")
include("neoforge")
include("forge")
