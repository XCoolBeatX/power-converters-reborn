# libs/

Drop the mod jars this project compiles against in here. They are **not** committed to the
repository — they belong to their respective authors and are not ours to redistribute (IC2
Classic in particular is released as "All rights reserved").

All three are `compileOnly`. They are never bundled into the built jar, and at runtime the mod
feature-detects each one, so it loads fine whether you have all of them, some, or none.

Rename them to exactly these filenames, because the `flatDir` repository in `build.gradle`
resolves them by `name-version.jar`:

| File | Get it from |
|---|---|
| `ic2classic-2.1.3.3.jar` | [IC2 Classic](https://www.curseforge.com/minecraft/mc-mods/industrial-craft-classic) — `IC2Classic-1.19.2-2.1.3.3.jar` |
| `buildcraft-8.0.10.jar` | BuildCraft Community Edition — `BuildCraft-Community-Edition-8.0.10+1.19.2.jar` |
| `jade-8.9.2.jar` | [Jade](https://www.curseforge.com/minecraft/mc-mods/jade) — `Jade-1.19.1-forge-8.9.2.jar` |

If you change versions, update `ic2_version`, `buildcraft_version` and `jade_version` in
`gradle.properties` to match.
