plugins {
    id("net.fabricmc.fabric-loom")
}

val modId: String = property("mod_id") as String
val minecraftVersion: String = property("minecraft_version") as String
val fabricLoaderVersion: String = property("fabric_loader_version") as String
val fabricApiVersion: String = property("fabric_api_version") as String
val modmenuVersion: String = property("modmenu_version") as String

base {
    archivesName.set("$modId-fabric")
}

// Compile the shared code straight into the loader jar. Minecraft 26.2 ships
// unobfuscated, so every loader sees identical names and the common sources need
// no remapping between platforms.
sourceSets.named("main") {
    java.srcDir(rootProject.file("common/src/main/java"))
    resources.srcDir(rootProject.file("common/src/main/resources"))
}

loom {
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    compileOnly("org.jetbrains:annotations:26.0.2")

    // Optional integration: the Mod Menu config-screen entrypoint is only wired up
    // when Mod Menu is actually installed.
    compileOnly("com.terraformersmc:modmenu:$modmenuVersion")
}

tasks.named<ProcessResources>("processResources") {
    val properties = mapOf(
        "id" to modId,
        "name" to project.property("mod_name"),
        "version" to project.version,
        "description" to project.property("mod_description"),
        "authors" to project.property("mod_authors"),
        "license" to project.property("mod_license"),
        "homepage" to project.property("mod_homepage"),
        "issues" to project.property("mod_issues"),
        "sources" to project.property("mod_sources"),
        "minecraft" to minecraftVersion,
        "fabric_loader" to fabricLoaderVersion,
        "java" to project.property("java_version"),
    )
    inputs.properties(properties)
    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}
