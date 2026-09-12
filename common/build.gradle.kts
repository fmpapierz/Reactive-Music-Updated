plugins {
    id("net.neoforged.moddev")
}

val neoformVersion: String = property("neoform_version") as String
val mixinVersion: String = property("mixin_version") as String
val modId: String = property("mod_id") as String

base {
    archivesName.set("$modId-common")
}

// Vanilla-mode: gives this module the plain Minecraft jar with no loader extensions,
// so everything compiled here is guaranteed to be loader-agnostic.
neoForge {
    neoFormVersion = neoformVersion
}

dependencies {
    compileOnly("org.spongepowered:mixin:$mixinVersion")
    compileOnly("org.jetbrains:annotations:26.0.2")
}

// The embedded songpack is shipped by the loader modules, which pull in this
// module's resource directory directly. Keeping it out of the common jar avoids
// copying ~280 MB of audio on every build of a module nobody ships.
tasks.named<ProcessResources>("processResources") {
    exclude("musicpack/**")
}
