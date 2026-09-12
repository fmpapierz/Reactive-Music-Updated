plugins {
    id("net.fabricmc.fabric-loom")
}

val modId: String = property("mod_id") as String
val minecraftVersion: String = property("minecraft_version") as String
val fabricLoaderVersion: String = property("fabric_loader_version") as String
val fabricApiVersion: String = property("fabric_api_version") as String
val quiltLoaderVersion: String = property("quilt_loader_version") as String
val modmenuVersion: String = property("modmenu_version") as String

base {
    archivesName.set("$modId-quilt")
}

// Quilt Loom has not been updated past Minecraft 1.21.x, so the Quilt jar is built
// with Fabric Loom. That is sound here: Quilt Loader bundles the whole Fabric loader
// API surface (net.fabricmc.api / net.fabricmc.loader.api), and Quilt's metadata layer
// feeds quilt.mod.json entrypoints through the same dispatcher, so a mod compiled
// against fabric-loader runs natively under Quilt.
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

    // Quilt Loader itself is only needed to compile against QuiltLoader/MinecraftQuiltLoader;
    // at runtime it is the loader, so it must never be bundled.
    compileOnly("org.quiltmc:quilt-loader:$quiltLoaderVersion")

    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("com.terraformersmc:modmenu:$modmenuVersion")
}

tasks.named<ProcessResources>("processResources") {
    val properties = mapOf(
        "id" to modId,
        "name" to project.property("mod_name"),
        "version" to project.version,
        "description" to project.property("mod_description"),
        "authors" to project.property("mod_authors"),
        "original_author" to project.property("mod_original_author"),
        "license" to project.property("mod_license"),
        "homepage" to project.property("mod_homepage"),
        "issues" to project.property("mod_issues"),
        "sources" to project.property("mod_sources"),
        "group" to project.property("mod_group"),
        "minecraft" to minecraftVersion,
        "quilt_loader" to quiltLoaderVersion,
        "java" to project.property("java_version"),
        "mixin_compat" to project.property("mixin_compat")
    )
    inputs.properties(properties)
    filesMatching("quilt.mod.json") {
        expand(properties)
    }
    filesMatching("reactivemusic-common.mixins.json") {
        expand(properties)
    }
}
