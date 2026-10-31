# 26.1.2 Fabric Port — Working Log

Branch `26.1-fabric`, from `1.21.1` (Design n' Decor 2.2b, Create 6.0.10, Minecraft 1.21.1, NeoForge). Goal: D&D on
**Fabric 26.1.2**, running on **Create Fly** (our fork at `../Create-Fly`), alongside the Steam 'n' Rails, Create
Aeronautics and Create Big Cannons ports.

For private server use only, with the authors' permission (code is MIT; art is under the D&D asset licence, which
limits redistribution). Don't publish builds publicly.

This file tracks the port's state so work can resume across sessions. Update it as you go.

## Starting point

| | |
|---|---|
| Upstream | `DrMango14/Create-Design-n-Decor`; fork `alec-scott/Create-Design-n-Decor` (`origin`) |
| Base branch | `1.21.1`: one NeoForge module, 121 Java files, 19 mixins, Registrate |
| Generated data | Committed in `src/generated/resources` (1.21 folder names already) |

Three ports at once, as for Steam 'n' Rails (see `../Create-Steam-n-Rails/PORTING-26.1.md`):
1. **NeoForge → Fabric.**
2. **Minecraft 1.21.1 → 26.1.2.**
3. **Create → Create Fly** (`com.zurrtum.create`, client code under `com.zurrtum.create.client.*`, Flywheel/Catnip/
   Ponder bundled inside it, no Registrate or datagen helpers).

## Building

```sh
export JAVA_HOME=/c/Users/akrsc/AppData/Roaming/PrismLauncher/java/java-runtime-epsilon   # Git Bash, JDK 25
./gradlew compileJava --console=plain
```

Create Fly comes from the local checkout's jar (`create_fly_jar` in `gradle.properties`); rebuild it there first.

## Decisions

- **Build**: the Steam 'n' Rails template (non-remap Loom 1.17, Java 25, Fabric API 0.155.3, Forge Config API Port
  26.1.5 for the NeoForge-style `ModConfigSpec` configs).
- **Registrate**: the relocated Fabric 26.1 Registrate and Porting Lib datagen stubs, copied from Steam 'n' Rails into
  `dev.lopyluna.dndecor.port.*` (the `CreateRegistrate` shim, `AssetLookup`/`BlockStateGen`/`TagGen`/...,
  `ConfigBase`, `ItemUseOverrides`). Same reason as there: two mods shipping the same class names is fragile.
- **Datagen stubbed**: the committed `src/generated/resources` are used. `DnDecorDatagen` and `content/datagen` are
  excluded from the build, and the datagen-only Registrate builder calls (`.recipe`, `.blockstate`, `.loot`,
  `.model`, plus `.addLayer` (render layers come from the quads in 26.1) and NeoForge's `.clientExtension`) were
  stripped from the registration code with a paren-balancing script. The generated JSON still needs converting to
  26.1 formats (see TODO).
- **Block entity types (Create Fly gap)**: Create Fly's block entities hardwire their type
  (`BeltBlockEntity(pos, state)` passes `AllBlockEntityTypes.BELT`), and 26.1 checks the block against the type in
  the constructor. D&D registers its own types for Create classes (belts, flywheels, cogwheels, flap displays,
  crushing wheels, millstones, the dyed container). `port/create/BETypeOverride` wraps the factory and
  `mixins/port/SmartBlockEntityMixin` swaps the type into `SmartBlockEntity`'s super call.
- **Inventories**: NeoForge capabilities → Create Fly's model. Belt and millstone types are registered with Fabric's
  transfer API through a cached behaviour, as Create Fly's `AllTransfer` does (`events/DnDecorCommon`); the dyed
  container block is an `ItemInventoryProvider`, like Create Fly's item vault, and builds the same
  `ConnectedItemVaultHandler`.
- **Dye Depot**: no 26.1 Fabric build, so its extra colours are never present (`isDyeDepotColor` returns false).
- **Missing Create classes**, recreated under `port/create/foundation`: `DyedBlockList`, a reduced `CopperBlockSet`
  (full blocks only, oxidation and waxing through Fabric's `OxidizableBlocksRegistry`), `CasingConnectivity` (forwards
  to Create Fly's client `AllCasings`). Create's `AllPaletteStoneTypes` is a static name → base block map in
  `MaterialTypeProvider`.
- **Dropped**: the vault-style placement sound silencing (NeoForge's entity-aware `getSoundType` and persistent
  data; Create Fly's item vault dropped it too).

## Port mechanics (how things map)

| 1.21.1 NeoForge | 26.1.2 Fabric here |
|---|---|
| `@Mod` constructor, mod event bus | `ModInitializer` / `ClientModInitializer`; `REG.register()` at the end of init |
| `DeferredRegister` creative tab + `BuildCreativeModeTabContentsEvent` | `FabricCreativeModeTab` + `CreativeModeTabEvents.modifyOutputEvent` |
| `ModContainer.registerConfig`, `ModConfigEvent` | Forge Config API Port `ConfigRegistry`, `ModConfigEvents` |
| `RegisterCapabilitiesEvent` | Fabric transfer API (`ItemStorage.SIDED`) / `ItemInventoryProvider` |
| `UseItemOnBlockEvent` (item-before-block) | `UseBlockCallback` |
| `RegisterEvent` aliases | `MappedRegistry.addAlias` after registration |
| `DeferredSoundType` | vanilla `SoundType` |
| `Tags.Items/Blocks` | Fabric `ConventionalItemTags` / `ConventionalBlockTags` |
| `ResourceLocation` | `Identifier` |
| `javax.annotation.Nullable` | JetBrains `@Nullable` |
| `REG.setTooltipModifierFactory` | client entrypoint registers `TooltipModifier`s (Create Fly's are client only) |

## Session log

### 2026-11-01
- Created `26.1-fabric`; build migrated; shims copied from Steam 'n' Rails; 165 Create/Catnip/Flywheel imports mapped
  (130 by path, 6 moved, 29 handled by hand or shims).
- Entry points, creative tab, configs, events, capabilities, sound types, registry aliases ported.
- First compile: 1656 error lines (symbol pass). After stripping datagen calls and the shims: 279 errors.

## TODO

- [ ] Finish compile: BE renderers (extract/submit), `SmartBlockEntity` read/write on `ValueInput`/`ValueOutput`,
      block method signatures, belt and flap display mixins retargeted at Create Fly.
- [ ] Convert `src/generated/resources` to 26.1 formats (Steam 'n' Rails' `porting-tools/convert_data_26.pl`:
      recipes, loot tables, advancements, tags) and generate `assets/dndecor/items/*.json` item definitions.
- [ ] Split connected-texture sheets into Create Fly's per-tile sprites (`SplitConnectedTextures.java`).
- [ ] Dyed recipes for Dye Depot colours carry `neoforge:conditions`; drop them or convert to `fabric:load_conditions`.
- [ ] In-game test: placement, belts, containers, cogwheels, flywheels, display boards, crushing wheels, millstones,
      text plates, levers, contraptions.
