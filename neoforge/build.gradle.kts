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
        "credits" to project.property("mod_credits"),
        "license" to project.property("mod_license"),
        "homepage" to project.property("mod_homepage"),
        "issues" to project.property("mod_issues"),
        "minecraft_range" to project.property("minecraft_version_range"),
        "javafml" to project.property("javafml_neoforge_range"),
        "loader" to project.property("neoforge_version_range"),
        "mixin_compat" to project.property("mixin_compat")
    )
    inputs.properties(properties)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(properties)
    }
    filesMatching("reactivemusic-common.mixins.json") {
        expand(properties)
    }
}
