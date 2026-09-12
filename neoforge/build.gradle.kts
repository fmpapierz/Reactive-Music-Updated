plugins {
    id("net.neoforged.moddev")
}

val modId: String = property("mod_id") as String
val neoforgeVersion: String = property("neoforge_version") as String

base {
    archivesName.set("$modId-neoforge")
}

sourceSets.named("main") {
    java.srcDir(rootProject.file("common/src/main/java"))
    resources.srcDir(rootProject.file("common/src/main/resources"))
}

neoForge {
    version = neoforgeVersion

    runs {
        register("client") {
            client()
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
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
        "javafml" to project.property("javafml_neoforge_range"),
        "loader" to project.property("neoforge_version_range"),
    )
    inputs.properties(properties)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(properties)
    }
    filesMatching("pack.mcmeta") {
        expand(properties)
    }
}
