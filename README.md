# Power Converters Reborn

A ground-up reimplementation of the classic **Power Converters** mod for **Minecraft 1.19.2 / Forge 43.5.0**.

The original stopped at 1.7.10. This rebuild keeps its design — a central **Energy Bridge**
with **Consumer** and **Producer** blocks attached to it — but targets the energy systems that
actually matter on 1.19.2.

## Supported systems

| System | Mod | Block pair |
|---|---|---|
| **EU** | IndustrialCraft 2 Classic | EU Consumer / EU Producer |
| **MJ** | BuildCraft Community Edition | MJ Consumer / MJ Producer |
| **FE** | Forge Energy — Thermal, Mekanism, AE2, Forestry, … | FE Consumer / FE Producer |

Forge Energy acts as the neutral hub unit, so **every pair of systems interoperates**: EU→MJ,
MJ→EU, EU→FE, and so on, without needing a converter block per combination.

## How to use it

1. Place an **Energy Bridge**.
2. Put a **Consumer** for the system you are converting *from* against any of its six faces.
3. Put a **Producer** for the system you are converting *to* against any other face.
4. Wire your cables/pipes/conduits into the consumer and producer as normal.

Any number of consumers and producers can share one bridge, in any arrangement. They all draw
on the same internal buffer.

```
[IC2 cable] -> [EU Consumer] -> [ENERGY BRIDGE] -> [MJ Producer] -> [BC pipe]
                                       |
                                [FE Producer] -> [Thermal conduit]
```

## Seeing what's in the buffer

Two ways, both showing the same numbers:

- **Right-click any block in the mod with an empty hand.** The attached bridge's contents
  appear on the action bar. Works with no other mods installed. An empty hand is required so
  that holding a block or a wrench still behaves normally.
- **Look at any block in the mod with [Jade](https://www.curseforge.com/minecraft/mc-mods/jade)
  installed.** Same readout, live in the hover tooltip — the easier option for watching a
  buffer fill in real time.

Both report from the bridge, so pointing at a *converter* shows the bridge it is attached to,
or tells you it isn't attached to one. The buffer is shown in all three currencies at once:

```
12,345 / 1,000,000 FE (1.23%)   = 3,086 EU  |  1,234.5 MJ
```

## Conversion rates

Defaults follow the long-standing community standard, and conversion is **lossless**:

| | |
|---|---|
| 1 EU | 4 FE |
| 1 MJ | 10 FE |
| 1 EU | 0.4 MJ |

Where those numbers come from, since it is worth being straight about it:

- **1 EU = 4 FE is well grounded.** It is IC2 Classic's own default — the mod ships
  `fluxBalance = 4` ("RF per EU Produced") in its config. Mekanism, GregTech CEu and Flux
  Networks all independently land on 4 as well. (The often-quoted `8` is the *1.12.2* default
  and is stale for 1.19.2.)
- **1 MJ = 10 FE is a convention, not a spec.** It comes from BuildCraft's own lead developer
  in 2014 ("for reference, 1 MJ = 10 RF"). Note that BuildCraft CE 8.0.10 ships *no* MJ↔FE
  conversion class at all, and the single place it does convert internally — its charging
  table — uses 1 MJ = 1 FE. Other bridge mods have picked 10:1 and 15:1. If 10 feels wrong for
  your pack, `fePerMj` is the knob.

Everything is configurable in `config/powerconverters-common.toml`:

- `fePerEu`, `fePerMj` — conversion ratios
- `bridgeCapacityFe` — buffer size of one bridge (default 1,000,000 FE)
- `maxTransferFePerTick` — throughput of a single converter block (default 20,000 FE/t);
  place more converters on the same bridge to scale beyond this
- `euTier` — IC2 voltage tier the EU blocks operate at (default 4 = EV/2048).
  **The EU Producer emits at this tier, so lower-tier cables will burn up.**

## Notes on correctness

Three details in the underlying APIs are easy to get wrong, and are handled explicitly:

- **BuildCraft measures MJ in microjoules** (`MjAPI.MJ == 1_000_000`). A naive port that treats
  MJ as a whole number is off by a factor of a million.
- **Both IC2 and BuildCraft return the *unused* remainder** from their accept/receive calls,
  not the amount taken. Verified by disassembling `MjBattery.addPowerChecking` and
  `LuminatorTileEntity.acceptEnergy` rather than assumed.
- All internal accounting is done in **micro-FE** (1 FE = 1,000,000 internal units) and always
  truncates rather than rounds, so partial transfers can neither be silently voided nor looped
  back and forth to duplicate energy.

IC2 and BuildCraft are both **optional** dependencies. Their integrations live behind lazily
constructed delegates, so the mod loads cleanly with either, both, or neither installed.

## Building

Requires **JDK 17** (Minecraft 1.19.2 / Forge 43.x will not build on anything newer).

The build compiles against three third-party mod jars, which are deliberately **not** committed
to this repository — they are not ours to redistribute. Put your own copies in `libs/` first;
see [libs/README.md](libs/README.md) for the exact filenames.

```sh
./gradlew build
```

The finished jar lands in `build/libs/`. All three dependencies are `compileOnly`, so none of
them are bundled into it.

## Licence

MIT — see [LICENSE](LICENSE).

An independent reimplementation, not a copy of the original mod's source. Credit for the
original concept belongs to the authors of Power Converters. This repository contains no part
of IndustrialCraft 2, BuildCraft, Jade, Forge or Minecraft; it only compiles against their
public APIs.
