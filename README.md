# Reactive Music

Reactive Music trades Minecraft's default music for something dynamic, reactive, and ever-changing. Including a collection of fantasy and celtic tracks to give an air of wonder, a whisper of the unknown, and the call of adventure.

Reactive Music is based off a music pack originally made for the Ambience mod back in 1.12, rebuilt as a standalone mod for modern Minecraft.

This repository updates the mod to **Minecraft 26.2**, built as a multiloader project targeting **Fabric, Quilt, NeoForge and Forge** from one shared codebase.

- Issues: <https://github.com/fmpapierz/Reactive-Music-Updated/issues>

## Project layout

| Module | Toolchain | Output |
| --- | --- | --- |
| `common` | ModDevGradle (vanilla / NeoForm) | shared, loader-agnostic code |
| `fabric` | Fabric Loom | `reactivemusic-fabric-<version>.jar` |
| `quilt` | Fabric Loom | `reactivemusic-quilt-<version>.jar` |
| `neoforge` | ModDevGradle | `reactivemusic-neoforge-<version>.jar` |
| `forge` | ForgeGradle 7 | `reactivemusic-forge-<version>.jar` |

`common` holds everything that does not touch a loader API: the song picker, the
songpack loader and parser, the audio player, the config screen and the mixins. Each
loader module compiles those shared sources directly into its own jar, so there is no
cross-module classpath to keep in sync.

Loader-specific behaviour is reached through two `ServiceLoader` interfaces in
`circuitlord.reactivemusic.platform`:

- `PlatformHelper` — game/config directories, mod presence, physical side.
- `BiomeTagHelper` — biome tag namespace (`c` on Fabric/Quilt/NeoForge, `forge` on Forge)
  and the per-platform tag name remapping.

### Why there is no mapping configuration

Minecraft 26.2 ships unobfuscated: the game jar carries its real names, Mojang no longer
publishes ProGuard mapping files, and Yarn stops at 1.21.11. Every loader therefore sees
the same class and method names, which is what makes a single shared source set possible
without any remapping step. It is also why the Mixin annotation processor is not applied
to the Forge module — its only job was generating obfuscation refmaps, and there is
nothing left to map.

## Building

Requires **JDK 25** (Minecraft 26.2 targets Java 25).

```bash
./gradlew buildAll
```

Jars are written to `<module>/build/libs/`. To build a single loader:

```bash
./gradlew :fabric:build
```

Each jar embeds the default songpack, so expect roughly 290 MB per jar.

### Development client

`:fabric`, `:neoforge` and `:forge` provide a `runClient` task. Quilt has no Loom release
for Minecraft 26.x, so the Quilt jar is built with Fabric Loom and has no dev client task;
test it by dropping the built jar into a Quilt instance. This works because Quilt Loader
bundles the Fabric loader API and feeds `quilt.mod.json` entrypoints through the same
dispatcher, so the mod loads natively rather than through a shim.

## Songpacks

Reactive Music ships a default songpack and can load user songpacks from the
`resourcepacks` folder. See [docs/MAKING_SONGPACKS.md](docs/MAKING_SONGPACKS.md) for the
songpack format, and `docs/` for downloadable templates.

## In-game

- `/reactivemusic` — open the config screen
- `/reactivemusic logBlockCounter` — dump the nearby-block counter
- `/reactivemusic toggleSoundEventLogging` — log every sound event to chat
- `/reactivemusic blacklistDimension` / `unblacklistDimension` — mute the mod in the current dimension
- `/reactivemusic toggleLogging` — echo the mod's decisions to chat

The config screen is also reachable from Mod Menu on Fabric and Quilt, and from the mod
list on NeoForge and Forge.

## Credits

Reactive Music was originally created by **CircuitLord** — original project:
<https://github.com/CircuitLord/ReactiveMusic>. All of the mod's design, its songpack
format and the bundled soundtrack are their work.

This Minecraft 26.2 multiloader update is maintained by **hooneybAdgers**.

Licensed under the GNU General Public License v3.0 — see [LICENSE](LICENSE).
