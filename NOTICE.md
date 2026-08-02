# Notice

## Relationship to the original mod

This project is an **independent reimplementation** for Minecraft 1.19.2, inspired by the
original Power Converters mod for 1.7.10 and earlier. It shares that mod's concept and block
layout, but contains **none of its source code** — everything here was written from scratch
against the current APIs. All credit for the original idea belongs to its authors.

## Third-party mods

This repository contains, and redistributes, **no part of** IndustrialCraft 2, BuildCraft,
Jade, Minecraft Forge or Minecraft. It only compiles against their public APIs.

The jars in `libs/` are deliberately excluded from version control. They belong to their
respective authors and are not ours to distribute — IC2 Classic in particular is released as
"All rights reserved". See [libs/README.md](libs/README.md) for how to obtain them.

At runtime every integration is feature-detected, so the mod loads cleanly whether you have
all of those mods, some of them, or none.
