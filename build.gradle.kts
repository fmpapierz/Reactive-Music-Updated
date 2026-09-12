plugins {
    id("java")
}

val javaVersion: String = property("java_version") as String
val modVersion: String = property("mod_version") as String
val modGroup: String = property("mod_group") as String
val minecraftVersion: String = property("minecraft_version") as String

allprojects {
    version = "$modVersion+$minecraftVersion"
    group = modGroup
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
        maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
        maven("https://maven.terraformersmc.com/releases/") { name = "Terraformers" }
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(javaVersion.toInt())
    }

    tasks.withType<Jar>().configureEach {
        // The embedded songpack contains hundreds of megabytes of audio; storing it
        // uncompressed keeps build times sane without meaningfully growing the jar,
        // since MP3 data does not deflate.
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

/**
 * Builds every loader jar in one go.
 */
tasks.register("buildAll") {
    group = "build"
    description = "Assembles the Fabric, Quilt, NeoForge and Forge jars."
    dependsOn(
        ":fabric:build",
        ":quilt:build",
        ":neoforge:build",
        ":forge:build",
    )
}
