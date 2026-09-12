plugins {
    id("net.minecraftforge.gradle")
}

val modId: String = property("mod_id") as String
val minecraftVersion: String = property("minecraft_version") as String
val forgeVersion: String = property("forge_version") as String

val mixinConfig = "$modId-common.mixins.json"

base {
    archivesName.set("$modId-forge")
}

sourceSets.named("main") {
    java.srcDir(rootProject.file("common/src/main/java"))
    resources.srcDir(rootProject.file("common/src/main/resources"))
}

minecraft {
    runs {
        configureEach {
            workingDir.convention(layout.projectDirectory.dir("run"))
            // Forge does not read mixin configs out of the mod metadata, so the config
            // is passed on the command line in dev and via the jar manifest when shipped.
            args("--mixin.config=$mixinConfig")
        }
        register("client")
    }
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$forgeVersion"))
    compileOnly("org.jetbrains:annotations:26.0.2")
}

// Minecraft 26.2 ships unobfuscated, so mixins resolve their targets directly and no
// refmap is produced. The Mixin annotation processor is therefore not applied here --
// it only knows how to emit searge/notch mappings and fails on unobfuscated targets.

tasks.named<Jar>("jar") {
    manifest {
        attributes["MixinConfigs"] = mixinConfig
    }
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
        "minecraft_range" to project.property("minecraft_version_range"),
        "pack_format" to project.property("resource_pack_format"),
        "javafml" to project.property("javafml_forge_range"),
        "loader" to project.property("forge_version_range"),
    )
    inputs.properties(properties)
    filesMatching("META-INF/mods.toml") {
        expand(properties)
    }
    filesMatching("pack.mcmeta") {
        expand(properties)
    }
}
