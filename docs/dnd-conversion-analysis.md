# D&D 3.5 Conversion Plan - Shattered Pixel Dungeon Adaptation Analysis

## Executive Summary

The original D&D conversion plan targets **original Pixel Dungeon** (simpler codebase, ~400 classes). Shattered Pixel Dungeon is a **vastly more complex fork** with:
- **1,255+ Java files** vs ~400 in original PD
- **6 hero classes** (not 4) with subclasses, talents, and armor abilities
- **50+ enemy types** vs ~20 in original
- **120+ buff types** vs ~30 in original
- **Multi-module Gradle project** vs simple Android project
- **Modern build system** (Gradle 8.13.2, Java 11+)

**Estimated effort increase: 3-5x original plan**

---

## Key Architectural Differences

### Build System Changes

| Aspect | Original PD Plan | Shattered PD Reality | Required Changes |
|--------|------------------|----------------------|------------------|
| **Build Tool** | Maven (proposed) | Gradle 8.13.2 (existing) | Use Gradle test plugins instead of Maven |
| **Structure** | Single module | Multi-module (SPD-classes, core, android, desktop, ios, services) | Test setup per module |
| **Java Version** | Java 8 | Java 11+ | Update test syntax |
| **Existing Tests** | None | None (confirmed) | Same starting point |

### Package Structure Changes

| Original PD | Shattered PD | Impact |
|-------------|--------------|--------|
| `com.watabou.pixeldungeon` | `com.shatteredpixel.shatteredpixeldungeon` | All import paths change |
| `PD-classes` | `SPD-classes` | Engine library name change |
| Single module | 6 modules | Test organization per module |

---

## Scope Expansion Required

### 1. Hero System Complexity

**Original PD Plan:**
- 1 class (Fighter/Warrior)
- Simple stats
- Basic progression

**Shattered PD Reality:**
```
6 Hero Classes:
├── Warrior
│   ├── Subclasses: Berserker, Gladiator
│   ├── Talents: 13 tiers
│   └── Abilities: Heroic Leap, Shockwave, Endure
├── Mage
│   ├── Subclasses: Battlemage, Warlock
│   ├── Talents: 13 tiers
│   └── Abilities: Elemental Blast, Wild Magic, Warp Beacon
├── Rogue
│   ├── Subclasses: Assassin, Freerunner
│   ├── Talents: 13 tiers
│   └── Abilities: Smoke Bomb, Death Mark, Shadow Clone
├── Huntress
│   ├── Subclasses: Sniper, Warden
│   ├── Talents: 13 tiers
│   └── Abilities: Spectral Blades, Spirit Hawk, Nature's Power
├── Duelist (NEW)
│   ├── Subclasses: Champion, Monk
│   ├── Talents: 13 tiers
│   └── Abilities: Challenge, Elemental Strike, Feint
└── Cleric (NEW)
    ├── Subclasses: Warpriest, Guardian
    ├── Talents: 13 tiers
    ├── Abilities: Trinity, Power of Many, Ascended Form
    └── 30+ Spells (entire spell system)
```

**Required Plan Changes:**
- Convert **6 classes** instead of 1, or choose 1 primary class
- Map talents to D&D feats/class features
- Convert armor abilities to D&D class abilities
- **Cleric spell system** would need conversion to D&D divine spells
- Subclass system maps well to D&D prestige classes

**Recommendation:** Start with **Warrior → Fighter** conversion as planned, but acknowledge 5 more classes remain.

### 2. Monster Complexity

**Original PD Plan:**
- 2 monsters (Dire Rat, Skeleton)
- Simple stat blocks
- One special ability (DR 5/bludgeoning)

**Shattered PD Reality:**
- **50+ unique mob types** across 5 dungeon regions
- **Champion enemy system** (Blessed, Growing, Projecting, etc.)
- Complex AI behaviors
- Special abilities per mob

**Key Mob Categories:**

| Region | Mobs | Complexity |
|--------|------|------------|
| **Sewers** | Rat, Gnoll (3 variants), Crab, Swarm, Slime | 7 types |
| **Prison** | Skeleton, Thief, Shaman (3 elements), Guard, Necromancer | 8 types |
| **Caves** | Bat, Brute, Spinner, DM-300 series, Elemental | 10+ types |
| **City** | Warlock, Monk, Golem, Dwarf | 8 types |
| **Halls** | Scorpio, Succubus, Eye, Yog-Dzewa | 10+ types |
| **Bosses** | Goo, Tengu, DM-300, Dwarf King, Yog-Dzewa | 5 complex |
| **Special** | Mimics (3 types), Wraith, Piranhas, Bees, etc. | 15+ types |

**Required Plan Changes:**
- Phase 5 needs **25x more work** (2 mobs → 50+ mobs)
- Each mob needs D&D stat block conversion
- Champion system needs D&D template conversion
- Boss mechanics need special D&D abilities

**Recommendation:** Start with 2-3 mobs per region (10-15 total) for PoC, not 2 total.

### 3. Item System Explosion

**Original PD Plan:**
- Basic weapons (3-5 types)
- Basic armor (5 tiers)
- Weapon damage types (3: slashing, piercing, bludgeoning)

**Shattered PD Reality:**

```
Items (core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/):
├── Weapons
│   ├── Melee (Tier 1-5, ~30 weapons)
│   ├── Missile weapons (darts, throwing)
│   └── Enchantments (~20 types)
├── Armor
│   ├── 5 tiers
│   └── Glyphs (~15 types)
├── Wands
│   └── 20+ unique wands with abilities
├── Rings
│   └── 10+ ring types
├── Artifacts
│   └── 30+ unique artifacts
├── Potions
│   ├── 15+ standard potions
│   └── Exotic potion variants
├── Scrolls
│   ├── 15+ standard scrolls
│   └── Exotic scroll variants
├── Food (10+ types)
├── Bombs (5+ types)
├── Stones/Runestones (15+ types)
├── Trinkets (new system)
└── Quest items
```

**Required Plan Changes:**
- Phase 4 needs **100x more work** for comprehensive conversion
- Wands need conversion to D&D wands/staffs
- Artifacts need conversion to D&D magic items
- Potions/Scrolls map to D&D consumables
- Enchantments/Glyphs need D&D magic item properties

**Recommendation:**
- PoC: 5 weapons, 3 armor types, skip artifacts/wands
- Full conversion: Plan 3-6 months just for items

### 4. Level/Dungeon System

**Original PD Plan:**
- Reduce from 26 to 3 levels
- Simple level generation
- No special rooms

**Shattered PD Reality:**

```
Levels (core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/):
├── 26 levels across 5 regions
├── Procedural generation
│   ├── Builders (algorithms)
│   └── Painters (decoration)
├── Rooms
│   ├── standard/ (10+ types)
│   ├── special/ (20+ types)
│   ├── secret/ (10+ types)
│   ├── quest/ (5+ types)
│   └── connection/ (5+ types)
├── Traps (20+ types)
├── Features (doors, chasms, grass, etc.)
└── Boss levels (5 unique layouts)
```

**Required Plan Changes:**
- Level generation is much more sophisticated
- Room variety requires more testing
- Quest system exists (Ghost, Wandmaker, Imp, etc.)
- Trap system needs D&D trap conversion

**Recommendation:** Keep 3-level PoC, but acknowledge full conversion needs all 26 levels.

---

## File Modification Impact

### Original Plan: ~15 core files

### Shattered PD Reality: **100-200+ files** for minimal PoC

**Core Files (Same as Original):**
1. `Char.java` - Ability scores, AC, d20 combat
2. `Hero.java` - Fighter stats, BAB, abilities
3. `HeroClass.java` - Fighter initialization
4. `Armor.java` - AC bonus
5. `Weapon.java` - Damage types, magic bonus
6. `Skeleton.java` - D&D stat block
7. `Rat.java` - Dire Rat conversion
8. `Bestiary.java` - Mob spawning
9. `Dungeon.java` - Level management
10. `WndHero.java` - UI for ability scores
11. `StatusPane.java` - AC display

**Additional Shattered PD Files:**
12. `Talent.java` - Map talents to D&D feats (500+ lines)
13. `ArmorAbility.java` - Convert to D&D class abilities
14. `HeroSubClass.java` - Subclass system
15. `Belongings.java` - Inventory with more slots
16. `Generator.java` - Item generation (more complex)
17. `Statistics.java` - Track D&D stats
18. `Badges.java` - Achievement system
19. `Buff.java` - Base buff class (120+ subclasses)
20-50. **30+ specific buff files** if converting buff mechanics
51-80. **30+ weapon files** if converting all weapons
81-130. **50+ mob files** if converting all mobs

**Package Changes:**
```bash
# All imports need updating:
com.watabou.pixeldungeon → com.shatteredpixel.shatteredpixeldungeon
```

---

## Testing Strategy Adaptation

### Phase 0: Setup Gradle Testing (NOT Maven)

**Original Plan:** Maven + JUnit + JaCoCo

**Shattered PD Approach:**

**File:** `core/build.gradle` (MODIFY existing)

Add test dependencies:
```gradle
apply plugin: 'java-library'

[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'
java.sourceCompatibility = java.targetCompatibility = appJavaCompatibility

dependencies {
    api project(':SPD-classes')
    implementation project(':services')

    // Add testing dependencies
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:4.11.0'
    testImplementation 'org.mockito:mockito-inline:4.11.0'
}

// Add JaCoCo for coverage
apply plugin: 'jacoco'

jacoco {
    toolVersion = "0.8.10"
}

test {
    useJUnit()
    testLogging {
        events "passed", "skipped", "failed"
        exceptionFormat "full"
    }
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.50
            }
        }
    }
}
```

**Test Directory Structure:**
```
core/
├── src/
│   ├── main/java/com/shatteredpixel/shatteredpixeldungeon/
│   └── test/java/com/shatteredpixel/shatteredpixeldungeon/
│       ├── actors/
│       │   ├── CharTest.java
│       │   ├── hero/
│       │   │   ├── HeroTest.java
│       │   │   └── TalentTest.java
│       │   └── buffs/
│       │       └── BuffTest.java
│       ├── combat/
│       │   ├── D20CombatTest.java
│       │   └── ArmorClassTest.java
│       └── items/
│           ├── weapon/
│           │   └── WeaponDamageTest.java
│           └── armor/
│               └── ArmorACTest.java
```

**Running Tests:**
```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# View coverage report
open core/build/reports/jacoco/test/html/index.html

# Run specific test class
./gradlew test --tests "D20CombatTest"

# Run all tests in a package
./gradlew test --tests "com.shatteredpixel.shatteredpixeldungeon.combat.*"
```

### Coverage Targets (Adjusted for Complexity)

**Core combat code (TDD - 90% target):**
- `Char.java` (core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java)
- `Hero.java` (core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java)
- Ability score calculations
- AC calculations
- d20 attack rolls

**Items & Monsters (Test-Alongside - 70% target):**
- Weapon damage types
- Armor AC bonuses
- Monster stat blocks
- DR mechanics

**Dungeon & UI (Manual - 30-40% target):**
- Level generation
- UI components
- Visual effects

**Overall project target:** 40-50% (lower than original 50-60% due to increased complexity)

---

## Phase-by-Phase Adaptations

### Phase 0: Build Setup

**Original:** Create Maven POM
**Shattered PD:** Modify existing Gradle build

**Changes:**
- Update `core/build.gradle` (NOT create `pom.xml`)
- Add JUnit 4.13.2 (or JUnit 5)
- Add JaCoCo plugin
- Create test directory: `core/src/test/java/`
- Update `.gitignore` for `build/` instead of `target/`

**New file:** `core/build.gradle` (existing, modify)

### Phase 1: Ability Scores

**Original:** Add to `Hero.java` and `Char.java`
**Shattered PD:** Same, but different package

**File locations:**
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java`

**Additional considerations:**
- Hero already has more complex stats (talent points, subclass, etc.)
- Serialization is more complex (Bundle system is the same)
- Need to handle interaction with talent system

**New complexity:**
- Talents may provide ability score bonuses
- Subclasses may have ability score requirements
- More buff types that affect ability scores

### Phase 2: Fighter Conversion

**Original:** Convert Warrior to Fighter
**Shattered PD:** Convert Warrior + handle subclasses

**File locations:**
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroClass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroSubClass.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Talent.java`

**Additional work:**
- Map **Berserker subclass** to D&D Barbarian or Berserker prestige class
- Map **Gladiator subclass** to D&D variant Fighter
- Convert **13 talent tiers** to D&D feats/class features
- Convert **3 armor abilities** to D&D special abilities

**Talent system example:**
```java
// Shattered PD has talents like:
- Hearty Meal (food heals more)
- Veteran's Intuition (reveal items in rooms)
- Test Subject (potion identification)
- Iron Will (resist debuffs)
// Need D&D equivalents (Endurance feat, Dungeoneering skill, etc.)
```

### Phase 3: D&D Combat

**Original:** Replace hit system with d20
**Shattered PD:** Same core changes, more interactions

**File location:**
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java`

**Additional considerations:**
- **120+ buffs** that affect combat (Vulnerable, Hex, Daze, etc.)
- **Champion enemies** with special abilities
- **Surprise attacks** and stealth mechanics
- **Armor abilities** that trigger in combat
- **Weapon enchantments** (~20 types) that proc

**Example enchantments to convert:**
- Blazing → D&D Flaming (+1d6 fire)
- Chilling → D&D Frost (+1d6 cold)
- Lucky → D&D Keen (improved critical)
- Vampiric → D&D Vampiric (life drain)

### Phase 4: Items

**Original:** 5 weapons, 5 armor tiers
**Shattered PD:** 30+ weapons, 5 armor + glyphs

**Major expansion needed:**

**Weapons** (core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/):
```
Tier 1 (d4-d6):
- WornShortsword, MagesStaff, Dagger, Gloves, Rapier

Tier 2 (d6-d8):
- Shortsword, HandAxe, Spear, Quarterstaff, Dirk, Sickle

Tier 3 (d8-d10):
- Sword, Mace, Scimitar, RoundShield, Sai, Whip

Tier 4 (d10-d12):
- Longsword, BattleAxe, Flail, RunicBlade, AssassinsBlade, Crossbow

Tier 5 (2d6-2d8):
- Greatsword, WarHammer, Glaive, Greataxe, GreatShield

Special:
- Katana, Gauntlet, RunicBlade, Sai, Nunchaku, Whip
```

**Each weapon needs:**
- D&D damage dice
- Damage type (slashing/piercing/bludgeoning)
- Weapon properties (reach, finesse, light, two-handed)
- Base attack bonus adjustments

**Armor Glyphs** need conversion to D&D armor properties:
- Stone → D&D DR 5/-
- Entanglement → D&D grasping armor
- Viscosity → D&D timeless armor
- Brimstone → D&D fire shield
- Etc. (15+ glyphs)

### Phase 5: Monsters

**Original:** 2 monsters
**Shattered PD:** 50+ monsters

**Minimal PoC subset (10-15 monsters):**

**Sewers (3):**
- Rat → Dire Rat (CR 1/3)
- Gnoll → Gnoll (CR 1)
- Crab → Monstrous Crab (CR 1/2)

**Prison (3):**
- Skeleton → Skeleton (CR 1/3)
- Guard → Human Warrior 2 (CR 1)
- Thief → Rogue 2 (CR 1)

**Caves (3):**
- Bat → Dire Bat (CR 2)
- Brute → Ogre (CR 3)
- Spinner → Monstrous Spider (CR 1)

**City (3):**
- Monk → Monk 3 (CR 2)
- Warlock → Sorcerer 3 (CR 2)
- Golem → Flesh Golem (CR 7)

**Halls (3):**
- Elemental → Medium Elemental (CR 3)
- Scorpio → Monstrous Scorpion (CR 3)
- Wraith → Wraith (CR 5)

**Each monster file requires:**
- Full D&D stat block (all 6 abilities)
- AC calculation
- Attack bonus (BAB + mods)
- Damage (weapon dice + mods)
- Special abilities (poison, DR, spell-like abilities, etc.)

**Example - GnollGeomancer complexity:**
```java
// Shattered PD has Gnoll with earth-moving abilities
// D&D conversion: Gnoll Druid 1 or Gnoll with Stonecunning
// Special attacks: rockfall trap, earth shield
// Spells: 0-level: light, resistance; 1st: entangle
```

### Phase 6: Dungeon Reduction

**Original:** 26 → 3 levels
**Shattered PD:** Same reduction, but more complex generation

**File locations:**
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Dungeon.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/RegularLevel.java`

**Additional considerations:**
- More sophisticated room generation (builders, painters)
- Quest system (Ghost, Wandmaker, Imp) - keep or remove?
- Secret rooms - keep for PoC?
- Special rooms (Shop, Alchemy) - remove for PoC?

**Recommendation:**
```java
// Simplified 3-level structure:
// Level 1: Sewers (Rats, Gnolls)
// Level 2: Prison (Skeletons, Guards)
// Level 3: LastLevel (Amulet)
```

### Phase 7: UI Updates

**Original:** Add ability scores to UI
**Shattered PD:** Same, but more complex UI

**File locations:**
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndHero.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/StatusPane.java`
- `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/BuffIndicator.java`

**Additional UI elements:**
- Talent screen needs updating
- Subclass selection screen
- Armor ability button
- More complex buff display (120+ buffs)

---

## Effort Estimation

### Original Plan Estimate: 40-60 hours for PoC

### Shattered PD Adjusted Estimate: **120-250 hours** for minimal PoC

**Breakdown:**

| Phase | Original Est. | Shattered PD Est. | Multiplier | Reason |
|-------|---------------|-------------------|------------|--------|
| **0. Setup** | 2 hours | 3 hours | 1.5x | Gradle vs Maven |
| **1. Ability Scores** | 4 hours | 6 hours | 1.5x | Talent interactions |
| **2. Fighter** | 6 hours | 15 hours | 2.5x | Subclasses + talents + abilities |
| **3. Combat** | 10 hours | 20 hours | 2x | 120+ buffs, champion system |
| **4. Items** | 8 hours | 40 hours | 5x | 30+ weapons, glyphs, enchantments |
| **5. Monsters** | 10 hours | 60 hours | 6x | 50+ mobs vs 2 |
| **6. Dungeon** | 6 hours | 15 hours | 2.5x | More complex generation |
| **7. UI** | 4 hours | 10 hours | 2.5x | More UI components |
| **Testing** | 10 hours | 40 hours | 4x | More code to test |
| **Integration** | 10 hours | 30 hours | 3x | More interactions |
| **TOTAL** | **60 hours** | **239 hours** | **4x** | Average multiplier |

**Realistic timeline:**
- **Part-time (10 hrs/week):** 24 weeks (~6 months)
- **Full-time (40 hrs/week):** 6 weeks (~1.5 months)

---

## Recommended Approach for Shattered PD

### Option 1: Minimal PoC (120-150 hours)

**Scope:**
- 1 class: Warrior → Fighter (skip subclasses, simplify talents)
- 10 monsters (2 per region)
- 10 weapons + 5 armor types (skip enchantments/glyphs)
- 3 levels
- Core d20 combat
- Skip: Talents, armor abilities, artifacts, wands, champion enemies

**Benefits:**
- Proves core D&D mechanics work
- Demonstrates d20 combat
- Validates AC/BAB system
- Achievable in 3-4 months part-time

### Option 2: Single-Class Complete (200-250 hours)

**Scope:**
- 1 class: Warrior with subclasses (Berserker, Gladiator)
- Convert talents to D&D feats
- Convert armor abilities to class features
- 15 monsters (3 per region)
- 20 weapons + armor (including enchantments)
- 3 levels

**Benefits:**
- Shows full class conversion
- Demonstrates talent → feat mapping
- More complete gameplay experience
- 5-6 months part-time

### Option 3: Full Conversion (500+ hours)

**Scope:**
- All 6 classes with subclasses
- All 50+ monsters
- All items (weapons, armor, artifacts, wands, etc.)
- All 26 levels
- Full D&D 3.5 rules

**Benefits:**
- Complete game
- All content converted
- Ready for release

**Timeline:**
- 12+ months full-time
- 2-3 years part-time

---

## Critical Decision Points

### 1. Talent System Conversion

**Shattered PD has 13 talent tiers per class** - this is a major system.

**Options:**
A. **Skip talents for PoC** (simpler)
B. **Map talents to D&D feats** (faithful conversion)
C. **Hybrid: Keep talent system, add D&D stats** (easiest)

**Recommendation:** Option A for PoC, Option B for full version.

### 2. Subclass Handling

**Shattered PD has 2 subclasses per hero class.**

**Options:**
A. **Skip subclasses for PoC** (simpler)
B. **Convert to D&D prestige classes** (faithful)
C. **Convert to D&D variant classes** (alternative)

**Recommendation:** Option A for PoC, Option B for full version.

### 3. Cleric Spell System

**Shattered PD has 30+ Cleric spells** - an entire casting system.

**Options:**
A. **Skip Cleric class entirely** (simplest)
B. **Convert spells to D&D divine spells** (huge effort)

**Recommendation:** Option A - focus on martial classes first (Warrior, Rogue, Huntress).

### 4. Artifact/Wand System

**30+ artifacts, 20+ wands** - complex item abilities.

**Options:**
A. **Skip for PoC** (simplest)
B. **Convert top 5-10 artifacts to D&D magic items**
C. **Convert wands to D&D wands/staffs**

**Recommendation:** Option A for PoC.

### 5. Champion Enemy System

**Champion enemies have special modifiers** (Blessed, Growing, etc.)

**Options:**
A. **Remove champion system** (simpler)
B. **Convert to D&D templates** (Advanced, Half-Dragon, etc.)

**Recommendation:** Option A for PoC, Option B for full version.

---

## Required Build File Changes

### 1. Core Module Test Setup

**File:** `core/build.gradle` (existing)

**Add after dependencies block:**
```gradle
dependencies {
    api project(':SPD-classes')
    implementation project(':services')

    // Testing dependencies
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:4.11.0'
}

// JaCoCo coverage plugin
apply plugin: 'jacoco'

jacoco {
    toolVersion = "0.8.10"
}

test {
    useJUnit()
    testLogging {
        events "passed", "skipped", "failed", "standardOut", "standardError"
        exceptionFormat "full"
    }
}

jacocoTestReport {
    dependsOn test

    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/ui/**',
                '**/sprites/**',
                '**/effects/**',
                '**/windows/**',
                '**/scenes/**'
            ])
        }))
    }
}

check.dependsOn jacocoTestCoverageVerification

jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = 'PACKAGE'
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.40
            }
        }
    }
}
```

### 2. Update .gitignore

**File:** `.gitignore` (existing, add if not present)

```
# Gradle test output
core/build/
SPD-classes/build/
android/build/
desktop/build/
ios/build/

# Test reports
**/build/reports/
**/build/test-results/
**/build/jacoco/

# Coverage data
**/.jacoco/
*.exec
```

---

## Test Examples for Shattered PD

### Ability Score Test

**File:** `core/src/test/java/com/shatteredpixel/shatteredpixeldungeon/actors/CharTest.java`

```java
package com.shatteredpixel.shatteredpixeldungeon.actors;

import org.junit.Test;
import static org.junit.Assert.*;

public class CharTest {

    @Test
    public void testAbilityModifierCalculation() {
        // Test with a concrete subclass since Char is abstract
        Char character = new com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat();

        character.STR = 18; assertEquals(4, character.strMod());
        character.STR = 16; assertEquals(3, character.strMod());
        character.STR = 14; assertEquals(2, character.strMod());
        character.STR = 12; assertEquals(1, character.strMod());
        character.STR = 10; assertEquals(0, character.strMod());
        character.STR = 8;  assertEquals(-1, character.strMod());
        character.STR = 6;  assertEquals(-2, character.strMod());
    }
}
```

### Fighter Test (with Talents)

**File:** `core/src/test/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroTest.java`

```java
package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HeroTest {

    private Hero fighter;

    @Before
    public void setUp() {
        fighter = new Hero();
        fighter.heroClass = HeroClass.WARRIOR;

        // Initialize as Fighter
        fighter.STR = 16;
        fighter.DEX = 13;
        fighter.CON = 14;
        fighter.INT = 10;
        fighter.WIS = 12;
        fighter.CHA = 8;
        fighter.HT = fighter.HP = 10 + fighter.conMod();
        fighter.attackSkill = 1;  // BAB +1
    }

    @Test
    public void testFighterStartingHP() {
        int expectedHP = 10 + 2;  // 10 base + 2 CON mod
        assertEquals(expectedHP, fighter.HP);
    }

    @Test
    public void testFighterBAB() {
        assertEquals(1, fighter.attackSkill);
    }

    @Test
    public void testFighterAbilityScores() {
        assertEquals(16, fighter.STR);
        assertEquals(13, fighter.DEX);
        assertEquals(14, fighter.CON);
    }
}
```

---

## Summary: Key Differences from Original Plan

| Aspect | Original PD | Shattered PD | Impact |
|--------|-------------|--------------|--------|
| **Build System** | Maven (new) | Gradle (existing) | Different setup |
| **Package Names** | com.watabou.pixeldungeon | com.shatteredpixel.shatteredpixeldungeon | All imports change |
| **File Count** | ~400 | ~1,255 | 3x more code |
| **Hero Classes** | 4 (1 for PoC) | 6 | 50% more |
| **Subclasses** | None | 2 per class | New system |
| **Talents** | None | 13 tiers per class | Major system |
| **Armor Abilities** | None | 3 per class | New mechanic |
| **Cleric Spells** | N/A | 30+ spells | Entire spell system |
| **Monsters** | ~20 (2 for PoC) | 50+ | 2.5x more |
| **Champions** | None | 6 types | New system |
| **Weapons** | ~10 | 30+ | 3x more |
| **Enchantments** | Basic | 20+ types | Complex system |
| **Armor Glyphs** | None | 15+ types | New system |
| **Artifacts** | Few | 30+ | Major system |
| **Wands** | Basic | 20+ unique | Complex abilities |
| **Buffs** | ~30 | 120+ | 4x more |
| **Levels** | 26 → 3 | 26 → 3 | Same reduction |
| **Room Types** | Basic | 50+ types | More complex |
| **UI Complexity** | Simple | Complex | More screens |
| **Estimated Effort** | 60 hours | 240 hours | **4x multiplier** |

---

## Final Recommendations

### For Proof of Concept (3-4 months):

1. **Use Gradle, not Maven** - leverage existing build system
2. **Start with Warrior only** - skip Duelist and Cleric
3. **Skip subclasses and talents** - add later
4. **Convert 10 monsters** - 2 per depth (Rat, Gnoll, Skeleton, Guard, Bat)
5. **Convert 10 weapons + 5 armor** - skip enchantments/glyphs
6. **3 levels only** - same as original plan
7. **Skip artifacts, wands, champion enemies** - focus on core combat
8. **Target 40% code coverage** - lower than original 50-60%

### For Full Conversion (1-2 years):

1. All 6 classes with subclasses
2. Talent → D&D feat mapping
3. Armor abilities → D&D class features
4. All 50+ monsters with D&D stat blocks
5. All weapons, armor, enchantments, glyphs
6. Artifacts → D&D magic items
7. Wands → D&D wands/staffs
8. Champion enemies → D&D templates
9. All 26 levels
10. Full D&D 3.5 rules (skills, feats, saving throws)

### Success Criteria for PoC:

✅ Core d20 combat works
✅ AC system replaces old defense
✅ BAB + ability mods for attacks
✅ Damage types (slashing/piercing/bludgeoning) matter
✅ Fighter feels like D&D Fighter
✅ 3 levels playable start to finish
✅ Win condition (collect amulet)
✅ 40%+ test coverage on core combat code

**The original plan is a good foundation, but needs 4x the effort for Shattered Pixel Dungeon.**
