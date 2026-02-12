# Shattered Pixel Dungeon - Codebase Architecture Overview

> **Version:** v3.3.5 (Build 890)
> **Framework:** LibGDX 1.14.0
> **Language:** Java 11+
> **Type:** Multi-module Gradle project

## Table of Contents

1. [Project Organization](#project-organization)
2. [Core Architecture](#core-architecture)
3. [Main Components](#main-components)
4. [Code Organization](#code-organization)
5. [Design Patterns](#design-patterns)
6. [Asset Organization](#asset-organization)
7. [Build System](#build-system)
8. [Platform Support](#platform-support)
9. [Testing](#testing)

---

## Project Organization

Shattered Pixel Dungeon is organized as a **multi-module Gradle project** with clear separation between game engine, game logic, and platform-specific implementations.

```
shattered-pixel-dungeon/
├── SPD-classes/              # Watabou engine library (80 files)
│   └── Graphics, input, game loop, OpenGL wrappers
├── core/                      # Main game logic (1,175 files)
│   └── Game mechanics, actors, items, levels, UI
├── android/                   # Android platform implementation
├── desktop/                   # Desktop launcher (LWJGL3)
├── ios/                       # iOS platform (RoboVM)
├── services/                  # Update/news service modules
│   ├── updates/              # Update checking (debug/GitHub)
│   └── news/                 # News feed (debug/production)
├── build.gradle              # Root build configuration
├── settings.gradle           # Module definitions
└── gradle.properties         # Build properties
```

### Module Responsibilities

| Module | Purpose | File Count |
|--------|---------|------------|
| **SPD-classes** | Core engine: rendering, input, game loop, utilities | 80 Java files |
| **core** | Game logic: actors, items, levels, UI, mechanics | 1,175 Java files |
| **android** | Android-specific launcher and platform integration | Platform code |
| **desktop** | Desktop launcher with LWJGL3 backend | Platform code |
| **ios** | iOS launcher with RoboVM compilation | Platform code |
| **services** | Pluggable update and news services | Service interfaces |

---

## Core Architecture

### Turn-Based Actor System

The game uses a **priority-based turn system** where all entities inherit from the `Actor` base class.

**Location:** `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Actor.java:25`

**Action Priority Levels:**
```
VFX (100)       → Visual effects (highest priority)
HERO (0)        → Player character
BLOB (-10)      → Area effects (fire, gas, etc.)
MOB (-20)       → Enemies
BUFF (-30)      → Status effects (lowest priority)
```

**Key Concepts:**
- Time spending: Each action consumes time units
- Cooldown management: Abilities have cooldown periods
- Turn scheduling: Actors queue actions based on priority

### State Management

**Global State (`Dungeon` class):**
- Manages current level, hero instance, game progress
- Handles level transitions and game state persistence
- Location: `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Dungeon.java`

**Scene System:**
- `GameScene`: Main gameplay rendering and input handling
- `TitleScene`: Title screen and menu
- Scene-based state transitions with fade effects

### Serialization & Save System

**Bundle System** (`com.watabou.utils.Bundle`):
- Key-value based serialization
- Supports primitives, objects, collections
- Version compatibility through class aliases
- Backward compatibility: v2.5.4+ saves supported

---

## Main Components

### 1. Actors (`/actors/`)

All in-game entities that act during gameplay.

**Base Classes:**
- `Actor`: Turn system base with time management
- `Char`: Character base (extends Actor) for hero and mobs
- `Blob`: Area effects (fire, gas, etc.)

**Key Subsystems:**

#### Hero System (`/actors/hero/`)
- `Hero.java`: Player character with inventory, stats, talents
- `HeroClass.java`: 4 hero classes (Warrior, Mage, Rogue, Huntress, Duelist, Cleric)
- `HeroSubClass.java`: Specialized subclass variants
- `Talent.java`: Skill tree and progression system
- `Belongings.java`: Inventory management
- `abilities/`: Class-specific armor abilities
  - `warrior/`: Heroic Leap, Shockwave, Endure
  - `mage/`: Elemental Blast, Wild Magic, Warp Beacon
  - `rogue/`: Smoke Bomb, Death Mark, Shadow Clone
  - `huntress/`: Spectral Blades, Spirit Hawk, Nature's Power
  - `duelist/`: Challenge, Elemental Strike, Feint
  - `cleric/`: Trinity, Power of Many, Ascended Form
- `spells/`: Cleric spell system (30+ spells)

#### Mobs (`/actors/mobs/`)
- `Mob.java`: Enemy base class with AI
- 50+ specific enemy types:
  - Early: Rat, Gnoll, Crab, Bandit
  - Mid: Brute, Shaman, Guard, Monk
  - Late: Golem, Warlock, Elemental, Scorpio
  - Special: Mimics (Golden, Crystal, Ebony), Wraith
  - Bosses: Goo, Tengu, DM-300, Dwarf King, Yog-Dzewa
- `npcs/`: Friendly NPCs (Blacksmith, Shopkeeper, Ghost, etc.)

#### Buffs (`/actors/buffs/`)
- `Buff.java`: Status effect base class
- 120+ buff types:
  - Positive: Bless, Haste, Barkskin, Invisibility
  - Negative: Poison, Burning, Paralysis, Weakness
  - Special: Preparation (Rogue), Combo (Warrior), Focus (Monk)
  - Hero-specific: Talent buffs, ability cooldowns

#### Blobs (`/actors/blobs/`)
- Area effects that spread across tiles
- Types: Fire, Toxic Gas, Confusion Gas, Freezing, Electricity
- Special: Alchemy cauldron, Well water, Foliage

### 2. Items (`/items/`)

**Base Class:** `Item.java` - All items inherit from this

**Major Categories:**

#### Equipment (`EquipableItem`)
- **Armor** (`/items/armor/`):
  - Cloth, Leather, Mail, Scale, Plate
  - Glyphs: Enchantments for armor (Stone, Entanglement, Viscosity, etc.)

- **Weapons** (`/items/weapon/`):
  - Melee weapons (tier 1-5)
  - Missile weapons (darts, throwing weapons)
  - Enchantments: Blazing, Chilling, Shocking, Lucky, etc.

- **Wands** (`/items/wands/`):
  - 20+ wand types (Magic Missile, Firebolt, Lightning, Disintegration, etc.)

#### Accessories
- **Rings** (`/items/rings/`): 10+ types (Energy, Evasion, Force, Haste, etc.)
- **Artifacts** (`/items/artifacts/`): 30+ unique items with special abilities

#### Consumables
- **Potions** (`/items/potions/`): 15+ types including exotic variants
- **Scrolls** (`/items/scrolls/`): 15+ types including exotic variants
- **Food** (`/items/food/`): Rations, meat, herbs
- **Bombs** (`/items/bombs/`): Various explosive types

#### Miscellaneous
- **Stones** (`/items/stones/`): Runestones for various effects
- **Trinkets** (`/items/trinkets/`): Special collectible items
- **Bags** (`/items/bags/`): Container items for organization
- **Quest Items** (`/items/quest/`): Special items for quests

**Item Generation:**
- `Generator.java`: Procedural item spawning with rarity tiers

### 3. Levels (`/levels/`)

**Base Classes:**
- `Level.java`: Abstract level base
- `RegularLevel.java`: Standard procedurally generated levels
- `Terrain.java`: Defines all terrain tile types

**Level Types:**
- Chapter levels: Sewers, Prison, Caves, City, Halls
- Boss levels: Specialized arenas for boss fights
- Special levels: Mining, Vault, Quest rooms

**Procedural Generation:**

#### Builders (`/levels/builders/`)
- Room placement algorithms
- Corridor generation
- Branch structures

#### Painters (`/levels/painters/`)
- Room decoration
- Terrain detail placement
- Visual variety

#### Rooms (`/levels/rooms/`)
- `Room.java`: Base room class
- **standard/**: Common room types (Hall, Tunnel, Cave, etc.)
- **special/**: Unique encounters (Shop, Library, Armory, etc.)
- **secret/**: Hidden rooms (Treasure, Shrine, etc.)
- **quest/**: Quest-specific rooms
- **connection/**: Room connectors and passages

#### Features (`/levels/features/`)
- `LevelTransition.java`: Stairs and level transitions
- `Door.java`: Door mechanics
- `Chasm.java`: Pit traps
- `HighGrass.java`: Vegetation interactions

#### Traps (`/levels/traps/`)
- 20+ trap types: Alarm, Fire, Poison, Teleportation, etc.

### 4. Scenes (`/scenes/`)

**Scene Hierarchy:**
- `PixelScene.java`: Base UI scene with camera management
- `GameScene.java`: Main gameplay loop and rendering
- `TitleScene.java`: Title screen and main menu
- `HeroSelectScene.java`: Character creation
- `JournalScene.java`: Lore and notes viewer
- `AlchemyScene.java`: Potion crafting interface
- Menu scenes: Settings, Rankings, About, etc.

### 5. UI (`/ui/`)

**Core UI Components:**
- `Toolbar.java`: Bottom action bar with attack/search buttons
- `HealthBar.java`: Character health display
- `BuffIndicator.java`: Active status effects display
- `GameLog.java`: Battle log and message feed
- `QuickSlotButton.java`: Quick item access system
- `Button.java`: Custom button base class
- `Window.java`: Modal window base class
- 60+ specialized UI components

### 6. Effects (`/effects/`)

**Visual Feedback:**
- `Speck.java`: Particle effects (flames, sparks, healing, etc.)
- `CellEmitter.java`: Tile-based particle emission
- `FloatingText.java`: Damage/healing numbers
- `Splash.java`: Blood, water, poison splashes
- `particles/`: Particle system implementations

### 7. Other Systems

#### Plants (`/plants/`)
- `Plant.java`: Base plant class
- 10+ types: Firebloom, Icecap, Earthroot, Sungrass, etc.
- Seed collection and planting mechanics

#### Mechanics (`/mechanics/`)
- `Ballistica.java`: Line-of-effect calculations for projectiles
- `ShadowCaster.java`: Line-of-sight algorithm for vision
- `PathFinder.java`: Pathfinding utilities (not in utils!)

#### Journal (`/journal/`)
- `Notes.java`: Player note tracking
- `Catalog.java`: Item encyclopedia
- `Lore.java`: Story and background lore

#### Sprites (`/sprites/`)
- `Sprite.java`: Base sprite animation
- `CharSprite.java`: Character sprite rendering
- `ItemSprite.java`: Item sprite rendering
- Sprite implementations for all visual entities

---

## Code Organization

### Package Structure (Core Module)

```
com.shatteredpixel.shatteredpixeldungeon/
├── [Top-level classes] (13 files)
│   ├── ShatteredPixelDungeon.java    - Main game class
│   ├── Dungeon.java                  - Game state manager
│   ├── Assets.java                   - Asset paths registry
│   ├── Badges.java                   - Achievement system
│   ├── SPDSettings.java              - User preferences
│   ├── Statistics.java               - Game statistics tracking
│   ├── Rankings.java                 - High score system
│   ├── Challenges.java               - Challenge mode flags
│   ├── GamesInProgress.java          - Save slot management
│   └── ...
│
├── actors/                           - All game entities
│   ├── Actor.java                    - Turn system base
│   ├── Char.java                     - Character base
│   ├── hero/                         - Player character
│   ├── mobs/                         - Enemies and NPCs
│   ├── buffs/                        - Status effects
│   └── blobs/                        - Area effects
│
├── items/                            - All items
│   ├── Item.java                     - Item base class
│   ├── armor/, weapon/, wands/       - Equipment
│   ├── rings/, artifacts/            - Accessories
│   ├── potions/, scrolls/            - Consumables
│   └── ...
│
├── levels/                           - Level generation
│   ├── Level.java, RegularLevel.java
│   ├── builders/                     - Generation algorithms
│   ├── painters/                     - Room decoration
│   ├── rooms/                        - Room types
│   ├── features/                     - Interactive elements
│   └── traps/                        - Trap types
│
├── scenes/                           - UI screens
├── ui/                               - UI components
├── effects/                          - Visual effects
├── sprites/                          - Sprite rendering
├── plants/                           - Plant system
├── mechanics/                        - Core algorithms
├── journal/                          - Journal system
├── windows/                          - Modal windows
├── messages/                         - Localization
└── utils/                            - Utility classes
```

### SPD-Classes Package Structure

```
com.watabou/
├── noosa/                   - Rendering framework (40+ files)
│   ├── Game.java           - Game lifecycle and main loop
│   ├── Scene.java          - Scene management
│   ├── Group.java          - Display container
│   ├── Visual.java         - Drawable base class
│   ├── Image.java          - Sprite rendering
│   ├── BitmapText.java     - Text rendering
│   ├── Camera.java         - View/camera management
│   ├── audio/              - Music and sound effects
│   ├── particles/          - Particle systems
│   ├── tweeners/           - Animation tweening
│   └── ui/                 - UI utilities
│
├── gltextures/             - Texture management
│   ├── Atlas.java          - Texture atlas handling
│   ├── SmartTexture.java   - Optimized texture loading
│   └── TextureCache.java   - Texture caching
│
├── glwrap/                 - OpenGL abstractions
│   ├── Shader.java         - GLSL shader programs
│   ├── Framebuffer.java    - Render targets
│   ├── Texture.java        - OpenGL texture wrapper
│   ├── Program.java        - Shader program management
│   └── Matrix.java         - Matrix transformations
│
├── glscripts/              - Shader scripts
│   └── Script.java         - Shader script management
│
├── input/                  - Input handling
│   ├── InputHandler.java   - Input event routing
│   ├── KeyBindings.java    - Keyboard mapping
│   ├── PointerEvent.java   - Touch/mouse events
│   ├── ControllerHandler.java - Gamepad support
│   └── GameAction.java     - Action binding
│
└── utils/                  - Engine utilities
    ├── Bundlable.java      - Serialization interface
    ├── Bundle.java         - Save file format
    ├── Random.java         - Random number generation
    ├── PathFinder.java     - Pathfinding algorithms
    ├── Point.java, PointF.java - 2D coordinates
    ├── Rect.java, RectF.java   - Rectangles
    ├── GameMath.java       - Math utilities
    └── ...
```

---

## Design Patterns

### Inheritance Hierarchies

#### Actor System
```
Actor (abstract)
    ├── Char (abstract)
    │   ├── Hero (player character)
    │   └── Mob (enemies)
    │       ├── Standard enemies (Rat, Gnoll, etc.)
    │       └── npcs/
    ├── Blob (area effects)
    └── Plant (environmental)
```

#### Item System
```
Item (base)
    ├── EquipableItem
    │   ├── Armor
    │   │   └── Glyph (enchantments)
    │   └── KindOfWeapon
    │       ├── Weapon
    │       │   └── Enchantment
    │       ├── MissileWeapon
    │       └── Wand
    ├── Potion (+ ExoticPotion)
    ├── Scroll (+ ExoticScroll)
    ├── Ring
    ├── Artifact
    ├── Bomb
    ├── Food
    ├── Bag (containers)
    └── [Quest items, stones, etc.]
```

#### Level System
```
Level (abstract)
    ├── RegularLevel (procedural dungeons)
    │   ├── SewerLevel, PrisonLevel
    │   ├── CavesLevel, CityLevel
    │   └── HallsLevel
    ├── [Boss levels]
    │   ├── SewerBossLevel, PrisonBossLevel, etc.
    └── [Special levels]
        ├── MiningLevel, LastLevel
        └── VaultLevel
```

#### Buff System
```
Buff (abstract)
    ├── FlavourBuff (display/timer only)
    ├── CounterBuff (tick-based)
    └── [Specific buffs]
        ├── Positive (Bless, Haste, etc.)
        ├── Negative (Poison, Burning, etc.)
        └── Hero-specific (Talent buffs)
```

### Patterns Used

| Pattern | Usage | Example Location |
|---------|-------|------------------|
| **Singleton** | Global state management | `Dungeon`, `GameScene`, `Assets` |
| **Factory** | Item/mob generation | `Generator.java`, mob spawning |
| **Strategy** | Behavior variation | Trap types, plant effects |
| **Template Method** | Level generation flow | `Level.create()`, builders |
| **Observer** | Event callbacks | Buff system, `Signal` class |
| **State** | Actor turn management | `Actor` state machine |
| **Flyweight** | Sprite/texture sharing | `TextureCache`, sprite atlases |
| **Command** | Action system | `HeroAction`, ability execution |
| **Decorator** | Item enchantments | Glyphs, weapon enchantments |

---

## Asset Organization

**Location:** `core/src/main/assets/`

```
assets/
├── sprites/               - Character & item pixel art
│   ├── avatars.png       - Hero portraits
│   ├── [mob sprites]     - 50+ enemy sprites
│   ├── [item sprites]    - Equipment, consumables
│   └── ... (200+ files)
│
├── interfaces/           - UI graphics
│   ├── toolbar.png       - Action bar
│   ├── buffs.png         - Status effect icons
│   ├── icons.png         - General UI icons
│   ├── chrome.png        - Window frames
│   └── ...
│
├── environment/          - Tilesets
│   ├── tiles/            - Level tilesets
│   ├── custom_tiles/     - Special tiles
│   └── terrain.png       - Base terrain
│
├── effects/              - Visual effects
│   └── Particle effects, spell visuals
│
├── fonts/                - Custom fonts
│   └── Pixel fonts for retro aesthetic
│
├── music/                - Background music (MP3/OGG)
│   └── Chapter-specific themes
│
├── sounds/               - Sound effects (OGG/WAV)
│   └── Combat, UI, ambient sounds
│
├── messages/             - Localized strings (20+ languages)
│   ├── actors/           - Character/mob names & descriptions
│   │   ├── actors.properties           (English)
│   │   ├── actors_es.properties        (Spanish)
│   │   ├── actors_fr.properties        (French)
│   │   ├── actors_de.properties        (German)
│   │   └── ... (20+ languages)
│   ├── items/            - Item descriptions
│   ├── levels/           - Level names & descriptions
│   ├── ui/               - UI text
│   ├── windows/          - Window/dialog text
│   ├── scenes/           - Scene text
│   ├── plants/           - Plant descriptions
│   ├── journal/          - Journal entries
│   └── misc/             - Miscellaneous text
│
├── splashes/             - Loading screens
│   └── title/            - Title screen images
│
└── gdx/                  - LibGDX UI assets
    ├── textfield.atlas   - Text input graphics
    └── textfield.json    - UI configuration
```

### Supported Languages

20+ languages via Transifex community translation:
- English (base)
- Spanish, French, German, Italian, Portuguese
- Russian, Ukrainian, Polish, Czech, Belarusian
- Chinese (Simplified & Traditional), Japanese, Korean, Vietnamese
- Indonesian, Turkish, Greek, Esperanto, Hungarian, Dutch, Swedish

---

## Build System

### Gradle Configuration

**Root Build:** `build.gradle`
- Gradle: 8.13.2
- Android Gradle Plugin: 8.13.2
- Java: 11+ (source & target compatibility)

**Version Information:**
```gradle
appVersionCode = 890
appVersionName = '3.3.5'
```

**Android Configuration:**
```gradle
appAndroidCompileSDK = 35   // Android 15
appAndroidMinSDK = 21       // Android 5.0+
appAndroidTargetSDK = 35    // Android 15
```

### Dependencies

**Core Dependencies:**
- **LibGDX:** 1.14.0 (cross-platform game framework)
- **GDX Controllers:** 2.2.4 (gamepad support)
- **RoboVM:** 2.3.24 (iOS compilation)
- **JSON:** 20170516 (data parsing)

**Module Dependencies:**
```
core
 ├── depends on: SPD-classes
 └── depends on: services

android
 └── depends on: core

desktop
 └── depends on: core

ios
 └── depends on: core
```

### Build Variants

**Debug Build:**
- Suffix: "-INDEV"
- Uses debug update/news services
- No obfuscation
- Faster build times

**Release Build:**
- R8 code shrinking and obfuscation
- ProGuard rules for optimization
- Production update/news services
- Signed APK for distribution

---

## Platform Support

### Android

**Configuration:** `android/build.gradle`
- Target SDK: 35 (Android 15)
- Minimum SDK: 21 (Android 5.0 Lollipop)
- Native libraries: ARM (v7a, v8a) and x86 (32/64-bit)

**Key Files:**
- `AndroidManifest.xml`: Permissions, activities, services
- `AndroidLauncher.java`: Platform initialization
- `AndroidPlatformSupport.java`: Platform-specific features
- ProGuard rules: Code obfuscation configuration

**Features:**
- Native Android file I/O
- Cloud save backup support
- Google Play integration ready

### Desktop

**Configuration:** `desktop/build.gradle`
- Backend: LWJGL3 (Lightweight Java Game Library)
- Java: 11+ required
- Packaging: jpackage for native installers

**Key Features:**
- Platform-specific: Windows, macOS, Linux
- Native dialogs via tinyFD
- XStartOnFirstThread for macOS compatibility
- Standalone JAR distribution

**Distribution:**
- Windows: `.exe` installer
- macOS: `.dmg` with app bundle
- Linux: `.deb`, `.rpm`, or AppImage

### iOS

**Configuration:** `ios/build.gradle`
- Backend: RoboVM 2.3.24
- Compiles Java to native iOS code
- App Store compatible

**Key Files:**
- `robovm.xml`: RoboVM configuration
- `Info.plist.xml`: iOS app metadata
- Native bindings for iOS APIs

---

## Testing

**Current State:** No automated tests present.

**Analysis:**
- ❌ No JUnit test files found
- ❌ No `src/test/` directories
- ❌ No test dependencies in `build.gradle` files
- ✅ Only standard `compileTestJava` encoding configurations

**Testing Approach:**
The project relies on **manual testing and playtesting** rather than automated unit tests. This is common for game projects where:
- Gameplay mechanics are experiential
- Procedural generation requires validation through play
- Player experience testing is more valuable than unit tests
- Visual and balance testing is subjective

**Potential Test Areas** (if adding tests):
- Core utilities (Random, PathFinder, Ballistica)
- Item generation (Generator)
- Level generation algorithms
- Save/load serialization (Bundle system)
- Game balance calculations

---

## Additional Notes

### Performance Considerations

- **Texture Atlases:** Sprites packed into atlases for efficient rendering
- **Object Pooling:** Particle effects use pooling to reduce GC
- **Lazy Loading:** Assets loaded on-demand to reduce memory
- **Level Streaming:** Only active level kept in memory

### Save System

**Location:** Game saves stored per platform
- Android: Internal storage
- Desktop: User home directory
- iOS: App sandbox

**Format:** Binary bundle format with JSON-like structure
- Backward compatible: v2.5.4+
- Class aliasing for version migration
- Compressed to reduce file size

### Modding Considerations

The codebase is **modding-friendly**:
- Open source (GPLv3)
- Clear class hierarchies for extension
- Generator system for custom items
- Room/level builders for custom content
- Message system for localization

**Common Mod Points:**
- New hero classes (`HeroClass`, `HeroSubClass`)
- Custom items (`Item` subclasses)
- New enemies (`Mob` subclasses)
- Custom levels (`Level` subclasses)
- New abilities (`ArmorAbility` subclasses)

---

## Quick Reference

### Important Base Classes

| Class | Location | Purpose |
|-------|----------|---------|
| `Actor` | `actors/Actor.java:25` | Turn system base |
| `Char` | `actors/Char.java` | Character base (hero/mobs) |
| `Item` | `items/Item.java` | Item base |
| `Level` | `levels/Level.java` | Level base |
| `Scene` | SPD-classes `noosa/Scene.java` | UI scene base |
| `Buff` | `actors/buffs/Buff.java` | Status effect base |
| `Mob` | `actors/mobs/Mob.java` | Enemy base |
| `Hero` | `actors/hero/Hero.java` | Player character |

### Key Systems Entry Points

| System | Entry Point |
|--------|-------------|
| **Game Launch** | `ShatteredPixelDungeon.java` (main) |
| **Turn Management** | `Actor.process()` |
| **Level Generation** | `Level.create()`, builders |
| **Item Generation** | `Generator.random()` |
| **Save/Load** | `Dungeon.saveAll()`, `Bundle` |
| **UI Rendering** | `GameScene.scene` |
| **Input** | `InputHandler`, `KeyBindings` |

---

**Document Version:** 1.0
**Last Updated:** 2026-02-12
**Codebase Version:** v3.3.5 (Build 890)
