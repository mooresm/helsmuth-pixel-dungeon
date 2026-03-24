# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Helsmuth Dungeon is a proof-of-concept conversion of [Shattered Pixel Dungeon](https://shatteredpixel.com/shatteredpd/) to [d20 System Compatible mechanics](https://www.d20srd.org/index.htm) (D&D 3.5 edition). The goal is to replace SPD's original percentage-based combat with d20 roll + modifier vs. AC resolution, ability scores, weapon damage types, and Damage Reduction.

**Phases 0–9 are complete.** See `docs/tasks.md` for the full roadmap and `docs/combat.md` for d20 integration details.

## Build & Test Commands

```bash
# Run unit tests (also generates JaCoCo coverage report)
./gradlew test

# Run a single test class
./gradlew test --tests "com.shatteredpixel.shatteredpixeldungeon.actors.CharTest"

# Build desktop debug build
./gradlew desktop:debug

# Build desktop release JAR
./gradlew desktop:release

# Build Android debug APK
./gradlew android:debug

# Generate coverage report only (requires tests to have run)
./gradlew jacocoTestReport
```

Coverage reports are written to `core/build/reports/jacocoHtml/index.html`. A minimum of 40% coverage is enforced by `jacocoTestCoverageVerification`.

## Module Architecture

This is a multi-module Gradle project:

```
SPD-classes/   ← Watabou LibGDX game engine (rendering, input, scene system, Bundle serialization)
    ↓
core/          ← All game logic: actors, items, levels, UI, D&D mechanics
    ↓
android/  desktop/  ios/   ← Platform launchers (thin wrappers over core)
services/      ← Update checking and news feed (optional dependency)
```

`SPD-classes` is a fork of Watabou's engine; treat it as a read-only upstream dependency unless fixing engine-level bugs.

## Key Architectural Patterns

**Turn system:** `Actor.java` uses a priority queue. Each actor's `act()` method is called in turn order; actors spend time via `spend(float)`. `Dungeon.java` is the global singleton for game state (current level, hero, depth).

**Character hierarchy:** `Char` → `Hero` / `Mob`. `Char.java` holds all D&D fields (ability scores, AC, BAB) and the d20 `hit()` method. `Hero.java` overrides `updateAC()`, `attackSkill()`, and `damageRoll()` for fighter-class behavior.

**Item system:** `Item` base class; `Weapon` → `MeleeWeapon`/`MissileWeapon` → specific weapon classes. Each weapon class declares its `DamageType` (SLASHING, PIERCING, BLUDGEONING) and dice notation.

**Buff/Glyph system:** Status effects extend `Buff`; armor enchantments extend `Armor.Glyph`; weapon enchantments extend `Weapon.Enchantment`. Applied via `Char.add(Buff)`.

**Level generation:** `Level` → `RegularLevel`. Builders (in `levels/builders/`) produce room graphs; painters (in `levels/painters/`) fill rooms with content.

**Serialization:** Everything save/load-able implements `storeInBundle(Bundle)` / `restoreFromBundle(Bundle)`. Bundle is a key-value store in `SPD-classes`.

## D&D Mechanics (Phases 0–5)

| Concept | Primary Class | Key Method/Field |
|---|---|---|
| Ability scores | `Char` | `STR, DEX, CON, INT, WIS, CHA` fields |
| Ability modifier | `Char` | `static statBonus(int stat)` → `(stat-10)/2` |
| Armor Class | `Char` | `AC` field; `Hero.updateAC()` recalculates |
| d20 attack roll | `Char` | `hit(attacker, defender, ...)` → `d20Hit(d20, bonus, ac)` |
| Natural 1/20 | `Char.d20Hit()` | Auto-miss on 1, auto-hit on 20 |
| Weapon damage type | `Weapon` | `DamageType` enum: SLASHING, PIERCING, BLUDGEONING |
| Damage Reduction | `Char` | `DR` field; checked in `Char.damage()` |
| Fighter BAB | `Hero` | `attackSkill` field (increments on level-up) |

## Unit Test Boundary

Tests live in `core/src/test/java/`. **Any code path that constructs an `Item` or accesses LibGDX resources (sprite sheets, textures, OpenAL) cannot be unit tested** — it will throw a static initializer error at `ItemSpriteSheet$Icons.<clinit>`. This includes `initHero()`, `initClass()`, and most item constructors. Use Mockito static mocks (`MockedStatic`) to stub `Dungeon`, `Challenges`, and `SPDSettings` where needed.

Current test files:
- `actors/CharTest.java` — ability modifier edge cases
- `actors/D20CombatTest.java` — d20 roll resolution, natural 1/20 handling
- `actors/hero/HeroTest.java` — Hero field defaults
- `items/weapon/WeaponDamageTypeTest.java` — weapon damage type and dice ranges

## Documentation

| File | Contents |
|---|---|
| `docs/tasks.md` | Phase-by-phase implementation checklist (phases 0–10) |
| `docs/combat.md` | Detailed d20 combat integration guide |
| `docs/overview.md` | Upstream SPD architecture reference |
| `docs/getting-started-desktop.md` | Desktop build/run setup |
| `docs/getting-started-android.md` | Android build/signing/distribution |
| `docs/recommended-changes.md` | Rebranding and customization guide |
