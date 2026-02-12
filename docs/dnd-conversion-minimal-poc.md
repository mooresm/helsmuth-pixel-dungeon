# D&D 3.5 Ultra-Minimal PoC - Shattered Pixel Dungeon

## Overview

**Goal:** Prove D&D 3.5 core mechanics work in Shattered Pixel Dungeon with absolute minimum content.

**Scope:**
- **1 class:** Warrior → D&D Fighter (no subclasses, no talents)
- **2 monsters:** Dire Rat + Skeleton (both CR 1/3)
- **2 levels:** Level 1 (combat) + Level 2 (amulet/victory)
- **3 weapons:** Dagger (1d4 piercing), Longsword (1d8 slashing), Mace (1d8 bludgeoning)
- **2 armor types:** Leather armor (+2 AC), Scale mail (+4 AC)
- **Core D&D mechanics:** Ability scores, d20 combat, AC, BAB, DR 5/bludgeoning

**Estimated effort:** 60-80 hours (1.5-2 months part-time)
**Optional Phase 10 (Branding):** +4 hours if preparing for public distribution

---

## Design Rationale

### Why 2 Levels?

**Level 1: Combat Tutorial**
- Spawns: Dire Rats (60%) + Skeletons (40%)
- Both are CR 1/3, appropriate for level 1 Fighter
- Teaches: Basic combat, weapon choice matters (DR 5/bludgeoning)
- Fighter starts at level 1, reaches level 2 by end

**Level 2: Victory**
- Amulet on pedestal (like original LastLevel)
- No enemies (or 1-2 remaining from Level 1 if desired)
- Collect amulet, return to stairs, win

### Why These Monsters?

**Dire Rat (CR 1/3):**
- Simple stat block (no special abilities)
- Fast, low HP (5 HP)
- Tests: Basic d20 combat, AC 15, DEX-based enemy
- **Drops:** Mystery Meat (33% chance) - raw food for healing

**Skeleton (CR 1/3):**
- DR 5/bludgeoning mechanic (critical D&D feature)
- Same CR as Dire Rat (balanced for level 1)
- Tests: Weapon damage types, slashing vs bludgeoning choice
- **Equipped with:** Dagger (1d4+1 piercing damage)
- **Drops:** Dagger (20% chance) - demonstrates piercing weapons

**Key teaching moment:** Player learns bludgeoning weapons bypass skeleton DR.

### Why These Weapons?

**3 weapons = 3 damage types:**

1. **Dagger** (1d4, piercing) - Starting weapon option, weak but fast
2. **Longsword** (1d8, slashing) - Classic Fighter weapon, good damage
3. **Mace** (1d8, bludgeoning) - Same damage as longsword, but bypasses skeleton DR

**Player discovers:** Longsword does 1-8 damage to rats, but skeletons reduce it by 5 (DR). Mace does full damage to both.

### Why 2 Armor Types?

1. **Leather armor** (+2 AC) - Light, starting option
2. **Scale mail** (+4 AC) - Medium, upgrade found on Level 1

**Simple progression:** AC 11 (10 + 1 DEX) → AC 13 (leather) → AC 15 (scale mail)

---

## Complete File Modification List

### Phase 0: Build Setup (2 hours)

**Files to modify:**

1. **`core/build.gradle`** - Add JUnit + JaCoCo
2. **`.gitignore`** - Add test output directories

**New directories:**
```
core/src/test/java/com/shatteredpixel/shatteredpixeldungeon/
├── actors/
│   └── CharTest.java
├── combat/
│   └── D20CombatTest.java
└── items/
    └── weapon/
        └── WeaponDamageTypeTest.java
```

### Phase 1: Ability Scores (6 hours)

**Files to modify:**

3. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java`**
   - Add: STR, DEX, CON, INT, WIS, CHA fields (6 ability scores)
   - Add: strMod(), dexMod(), conMod(), intMod(), wisMod(), chaMod() methods

4. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java`**
   - Add: Same 6 ability scores (override from Char if needed)
   - Update: storeInBundle() to save abilities
   - Update: restoreFromBundle() to load abilities

**Tests to create:**
- `CharTest.java` - Test ability modifier calculation

### Phase 2: Fighter Initialization (8 hours)

**Files to modify:**

5. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroClass.java`**
   - Update: WARRIOR.initHero() method
     - Set ability scores: STR 16, DEX 13, CON 14, INT 10, WIS 12, CHA 8
     - Set starting HP: 10 + conMod() = 12
     - Set starting BAB: attackSkill = 1
   - Update: WARRIOR perks description to mention D&D Fighter

6. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java`**
   - Update: onLevelUp() - HP gain = 1d10 + conMod()
   - Update: onLevelUp() - BAB increases: attackSkill++

**Tests to create:**
- `HeroTest.java` - Test Fighter starting stats and level up

### Phase 3: D&D Combat System (12 hours)

**Files to modify:**

7. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java`**
   - Replace: hit() method with d20 system:
     ```java
     public static boolean hit(Char attacker, Char defender, boolean magic) {
         if (magic) return true;

         int d20 = Random.IntRange(1, 20);
         if (d20 == 1) return false;  // Auto-miss
         if (d20 == 20) return true;  // Auto-hit

         int attackBonus = attacker.attackSkill(defender);
         int totalAttack = d20 + attackBonus;
         int defenderAC = defender.armorClass();

         return totalAttack >= defenderAC;
     }
     ```
   - Add: armorClass() method:
     ```java
     public int armorClass() {
         return 10 + dexMod();  // Base implementation
     }
     ```
   - Update: dr() method signature to accept attacker:
     ```java
     public int dr(Char attacker) {
         return 0;  // Default, override in subclasses
     }
     ```
   - Update: attack() method to pass attacker to dr():
     ```java
     int dr = enemy.dr(this);
     ```

8. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java`**
   - Update: attackSkill() - BAB + STR mod (+ weapon bonus):
     ```java
     @Override
     public int attackSkill(Char target) {
         int bonus = attackSkill;  // BAB (now represents level-based bonus)

         KindOfWeapon wep = belongings.weapon;
         if (wep == null || wep.isMelee()) {
             bonus += strMod();  // Melee uses STR
         } else {
             bonus += dexMod();  // Ranged uses DEX
         }

         if (wep != null) {
             bonus += wep.level();  // Magic weapon bonus
         }

         return bonus;
     }
     ```
   - Update: damageRoll() - weapon damage + STR mod:
     ```java
     @Override
     public int damageRoll() {
         KindOfWeapon wep = belongings.weapon;
         int dmg;

         if (wep != null) {
             dmg = wep.damageRoll(this);
         } else {
             dmg = Random.IntRange(1, 3);  // Unarmed: 1d3
         }

         // Add STR modifier for melee
         if (wep == null || wep.isMelee()) {
             dmg += strMod();
         }

         return Math.max(dmg, 1);  // Minimum 1 damage
     }
     ```
   - Override: armorClass() - includes armor:
     ```java
     @Override
     public int armorClass() {
         int ac = 10 + dexMod();

         if (belongings.armor != null) {
             ac += belongings.armor.ACBonus();
         }

         return ac;
     }
     ```

**Tests to create:**
- `D20CombatTest.java` - Test d20 rolls, AC calculation, attack bonuses

### Phase 4: Weapon System (10 hours)

**Files to modify:**

9. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/Weapon.java`**
   - Add: DamageType enum:
     ```java
     public enum DamageType {
         SLASHING,
         PIERCING,
         BLUDGEONING
     }
     ```
   - Add: damageType field and getter:
     ```java
     protected DamageType damageType = DamageType.SLASHING;

     public DamageType getDamageType() {
         return damageType;
     }
     ```

10. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Dagger.java`**
    - Update: min() = 1, max() = 4  (1d4)
    - Set: damageType = DamageType.PIERCING

11. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Longsword.java`**
    - Update: min() = 1, max() = 8  (1d8)
    - Set: damageType = DamageType.SLASHING

12. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/melee/Mace.java`**
    - Update: min() = 1, max() = 8  (1d8)
    - Set: damageType = DamageType.BLUDGEONING

**Tests to create:**
- `WeaponDamageTypeTest.java` - Test damage dice and damage types

### Phase 5: Armor System (4 hours)

**Files to modify:**

13. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/Armor.java`**
    - Add: ACBonus() method:
      ```java
      public int ACBonus() {
          switch (tier) {
              case 1: return 1;  // Cloth (but we're using as leather)
              case 2: return 2;  // Leather
              case 3: return 4;  // Scale mail
              case 4: return 5;  // Chainmail
              case 5: return 8;  // Plate
              default: return 0;
          }
      }
      ```

14. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/LeatherArmor.java`**
    - Verify: tier = 2 (AC bonus +2)

15. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/armor/ScaleArmor.java`**
    - Verify: tier = 3 (AC bonus +4)

**Tests to create:**
- `ArmorTest.java` - Test AC bonuses by tier

### Phase 6: Monster Conversions (12 hours)

**Files to modify:**

16. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Rat.java`**
    - Rename conceptually to "Dire Rat"
    - Update: name = "dire rat"
    - Update: HP = HT = 5  (1d8+1)
    - Set abilities: STR 10, DEX 17, CON 12, INT 2, WIS 12, CHA 4
    - Update: defenseSkill → not used (replaced by AC)
    - Add: armorClass() = 15  (10 + 3 DEX + 1 natural + 1 size)
    - Add: attackSkill():
      ```java
      @Override
      public int attackSkill(Char target) {
          return 0 + 3 + 1;  // BAB 0 + DEX 3 + size 1 = +4
      }
      ```
    - Update: damageRoll() = 1d4 (bite):
      ```java
      @Override
      public int damageRoll() {
          return Random.IntRange(1, 4);
      }
      ```
    - Add: dr():
      ```java
      @Override
      public int dr(Char attacker) {
          return 0;  // No DR
      }
      ```
    - **Add: Loot drop (Mystery Meat)**:
      ```java
      {
          // In constructor
          loot = MysteryMeat.class;
          lootChance = 0.33f;  // 33% chance to drop raw meat
      }
      ```

17. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Skeleton.java`**
    - Update: HP = HT = 6  (1d12)
    - Set abilities: STR 13, DEX 13, CON 10, INT 10, WIS 10, CHA 1
    - Update: defenseSkill → not used
    - Add: armorClass() = 15  (10 + 2 natural + 1 DEX + 2 armor)
    - Add: attackSkill():
      ```java
      @Override
      public int attackSkill(Char target) {
          return 0 + strMod();  // BAB 0 + STR 1 = +1
      }
      ```
    - Update: damageRoll() = 1d4+1 (dagger + STR):
      ```java
      @Override
      public int damageRoll() {
          return Random.IntRange(1, 4) + strMod();  // Dagger (1d4) + STR mod
      }
      ```
    - **Add: DR 5/bludgeoning** (KEY FEATURE):
      ```java
      @Override
      public int dr(Char attacker) {
          // Check if attacker is using bludgeoning weapon
          if (attacker instanceof Hero) {
              Hero hero = (Hero) attacker;
              KindOfWeapon weapon = hero.belongings.weapon;

              if (weapon instanceof Weapon) {
                  Weapon w = (Weapon) weapon;
                  if (w.getDamageType() == Weapon.DamageType.BLUDGEONING) {
                      return 0;  // Bludgeoning bypasses DR
                  }
              } else if (weapon == null) {
                  // Unarmed is bludgeoning (fists)
                  return 0;
              }
          }

          return 5;  // Full DR 5 vs non-bludgeoning
      }
      ```
    - **Add: Loot drop (Dagger)**:
      ```java
      {
          // In constructor
          loot = Dagger.class;
          lootChance = 0.20f;  // 20% chance to drop dagger
      }
      ```

**Tests to create:**
- `RatTest.java` - Test Dire Rat D&D stats
- `SkeletonTest.java` - Test Skeleton D&D stats
- `SkeletonDRTest.java` - Test DR 5/bludgeoning mechanic

### Phase 7: Level System (8 hours)

**Files to modify:**

18. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Dungeon.java`**
    - Update: newLevel() method:
      ```java
      public static Level newLevel() throws Exception {
          Dungeon.level = null;
          Actor.clear();

          depth++;
          if (depth > 2) depth = 2;  // Cap at depth 2

          Level level;
          switch (depth) {
              case 1:
                  // Combat level with rats and skeletons
                  level = new SewerLevel();
                  break;
              case 2:
                  // Victory level with amulet
                  level = new LastLevel();
                  break;
              default:
                  level = new DeadEndLevel();
          }

          level.create();
          Statistics.deepestFloor = Math.max(Statistics.deepestFloor, depth);

          return level;
      }
      ```
    - Update: bossLevel() = false (no bosses)
    - Update: shopOnLevel() = false (no shops)

19. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Bestiary.java`**
    - Update: mobClass() for 2-level system:
      ```java
      private static Class<?> mobClass(int depth) {
          switch (depth) {
              case 1:
                  // Level 1: 60% rats, 40% skeletons
                  return Random.Int(100) < 60 ? Rat.class : Skeleton.class;
              case 2:
                  // Level 2: No spawns (amulet level)
                  return null;
              default:
                  return null;
          }
      }
      ```
    - Update: isBoss() = false

**Tests to create:**
- `DungeonDepthTest.java` - Test 2-level cap
- `BestiaryTest.java` - Test mob spawning logic

### Phase 8: UI Updates (6 hours)

**Files to modify:**

20. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndHero.java`**
    - Add ability score display in stats section:
      ```java
      // After existing stat slots:
      statSlot("STR", String.valueOf(hero.STR) + " (+" + hero.strMod() + ")");
      statSlot("DEX", String.valueOf(hero.DEX) + " (+" + hero.dexMod() + ")");
      statSlot("CON", String.valueOf(hero.CON) + " (+" + hero.conMod() + ")");
      statSlot("INT", String.valueOf(hero.INT) + " (+" + hero.intMod() + ")");
      statSlot("WIS", String.valueOf(hero.WIS) + " (+" + hero.wisMod() + ")");
      statSlot("CHA", String.valueOf(hero.CHA) + " (+" + hero.chaMod() + ")");
      statSlot("AC", String.valueOf(hero.armorClass()));
      statSlot("BAB", "+" + hero.attackSkill);
      ```

21. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/StatusPane.java`**
    - Add AC display next to HP (or replace old defense display)

22. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/WelcomeScene.java`** (optional)
    - Add note about D&D conversion in welcome text

**Tests:**
- Manual UI testing only (expensive to automate)

### Phase 9: Starting Equipment (2 hours)

**Files to modify:**

23. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroClass.java`**
    - Update: WARRIOR.initHero() starting equipment:
      ```java
      // Give player choice or provide one weapon:
      (hero.belongings.weapon = new Longsword()).identify();
      (hero.belongings.armor = new LeatherArmor()).identify();

      // Add mace to starting inventory for skeleton DR testing
      new Mace().identify().collect();

      // Standard rations
      new Food().identify().collect();
      ```

---

### Phase 10: Project Branding & Distribution Prep (4 hours)

**Note:** This phase is **OPTIONAL** and should only be completed **AFTER** the PoC is successful and you're preparing for public distribution.

**Files to modify:**

24. **`build.gradle` (root)** - Application identity
    - Change: `appName = "Your D&D Pixel Dungeon"` (or your chosen name)
    - Change: `appPackageName = "com.yourname.yourproject"` (must be unique)
    - Update: `appVersionName = "0.1.0-alpha"` (or your versioning scheme)
    - Keep: `appVersionCode` at Shattered's current value or higher (don't decrement)
    - Note: If setting version code to 1, review compatibility code in ShatteredPixelDungeon.java

25. **`core/src/main/assets/interfaces/banners.png`** - Title screen graphics
    - Replace: Main title graphic with your game's name/logo
    - Replace: Glow layer for title effect
    - Maintain: Same image dimensions and format

26. **Application Icons** - Replace all platform icons
    - `android/src/debug/res/` - Android debug icons (mipmap-*/ic_launcher.png, multiple sizes)
    - `android/src/main/res/` - Android release icons (mipmap-*/ic_launcher.png, multiple sizes)
    - `desktop/src/main/assets/icons/` - Desktop application icons
    - `ios/assets/Assets.xcassets/` - iOS icons (if targeting iOS)

27. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/AboutScene.java`** - Credits
    - Add: Your name and role to credits section
    - Maintain: All existing Shattered Pixel Dungeon credits (GPLv3 requirement)
    - Add: Link to your project repository
    - Note: Cannot remove existing credits per GPLv3 license

28. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/SupporterScene.java`** - Supporter links
    - Update: Links to your own support platform (if desired)
    - Or: Disable entirely if not seeking support

29. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/TitleScene.java`** - UI buttons
    - Option A: Disable supporter button by commenting out `add(btnSupport);`
    - Option B: Disable news button by commenting out `add(btnNews);`
    - Warning: Google Play prohibits Patreon mentions - remove if releasing there

30. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSupportPrompt.java`** - Support prompt
    - Update: Text and links for your project
    - Or: Disable entirely if not seeking support

31. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/keys/WornKey.java`** - Prompt trigger
    - Disable: Support prompt trigger if not using

32. **`desktop/build.gradle`** - Desktop update notifications
    - Change: `:services:updates:githubUpdates` to `:services:updates:debugUpdates` (disables updates)
    - Or: Keep githubUpdates and modify GitHubUpdates.java to point to your repo

33. **`android/build.gradle`** - Android update notifications
    - Change: `:services:updates:githubUpdates` to `:services:updates:debugUpdates` (disables updates)
    - Or: Keep githubUpdates and modify GitHubUpdates.java to point to your repo

34. **`services/updates/githubUpdates/src/main/java/.../GitHubUpdates.java`** (optional)
    - Update: URL to your GitHub releases API:
      ```java
      httpGet.setUrl("https://api.github.com/repos/yourusername/yourrepo/releases");
      ```
    - Note: Requires specific release format (title, body with `---`, `internal version number: #`)

35. **`services/news/shatteredNews/src/main/java/.../ShatteredNews.java`** (optional)
    - Update: URLs to point to your atom/xml news feed
    - Adjust: Parsing logic if your feed format differs

36. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/messages/Languages.java`** - Translations
    - Option A (Remove all non-English): Delete all enum constants except ENGLISH
    - Option B (Keep some): Remove only unwanted language enums
    - Option C (Change base language): Remove English enums, rename your language files

37. **`core/src/main/assets/messages/`** - Translation resource files
    - Option A: Remove all `*_XX.properties` files (XX = language code like ru, es, zh)
    - Option B: Keep only desired language files
    - Note: Keep base `*.properties` files (no underscore+code)

38. **`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSettings.java`** - Language picker
    - If removing translations: Comment out `add( langs );` and `add( langsTab );`
    - If keeping translations: Leave language picker enabled

**Validation:**
- [ ] Game compiles and builds for all target platforms
- [ ] New app name and icon appear correctly
- [ ] Credits display correctly with your attribution
- [ ] Update notifications don't point to Shattered's repository
- [ ] Language settings match your translation strategy
- [ ] GPLv3 compliance: Original credits intact, source code available

**Distribution Prep:**
- [ ] Create public GitHub repository for your fork
- [ ] Add comprehensive README explaining D&D conversion
- [ ] Include LICENSE file (GPLv3)
- [ ] Document build instructions
- [ ] Create initial release/tag if appropriate

---

## Complete Test Suite

### Test Files to Create (16 tests total)

**1. `core/src/test/java/.../actors/CharTest.java`**
```java
@Test public void testAbilityModifiers() { ... }
@Test public void testArmorClassBase() { ... }
```

**2. `core/src/test/java/.../actors/hero/HeroTest.java`**
```java
@Test public void testFighterStartingStats() { ... }
@Test public void testFighterStartingHP() { ... }
@Test public void testFighterLevelUp() { ... }
@Test public void testFighterBABProgression() { ... }
```

**3. `core/src/test/java/.../combat/D20CombatTest.java`**
```java
@Test public void testD20AttackRoll() { ... }
@Test public void testNatural1Misses() { ... }
@Test public void testNatural20Hits() { ... }
@Test public void testACCalculation() { ... }
@Test public void testAttackBonusCalculation() { ... }
```

**4. `core/src/test/java/.../items/weapon/WeaponTest.java`**
```java
@Test public void testDaggerDamage() { ... }
@Test public void testLongswordDamage() { ... }
@Test public void testMaceDamage() { ... }
@Test public void testDamageTypes() { ... }
```

**5. `core/src/test/java/.../items/armor/ArmorTest.java`**
```java
@Test public void testLeatherArmorACBonus() { ... }
@Test public void testScaleArmorACBonus() { ... }
```

**6. `core/src/test/java/.../actors/mobs/RatTest.java`**
```java
@Test public void testDireRatStats() { ... }
@Test public void testDireRatAC() { ... }
@Test public void testDireRatDamage() { ... }
```

**7. `core/src/test/java/.../actors/mobs/SkeletonTest.java`**
```java
@Test public void testSkeletonStats() { ... }
@Test public void testSkeletonDR_WithSlashing() { ... }
@Test public void testSkeletonDR_WithBludgeoning() { ... }
@Test public void testSkeletonDR_Unarmed() { ... }
```

---

## Build Configuration

### `core/build.gradle` (add after dependencies block)

```gradle
dependencies {
    api project(':SPD-classes')
    implementation project(':services')

    // Testing
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:4.11.0'
}

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
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}

check.dependsOn jacocoTestCoverageVerification

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.40
            }
        }
    }
}
```

### `.gitignore` (add)

```
# Gradle test output
**/build/
**/build/reports/
**/build/test-results/

# Coverage
*.exec
```

---

## Testing Commands

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# View coverage report
open core/build/reports/jacoco/test/html/index.html
# Or: xdg-open core/build/reports/jacoco/test/html/index.html

# Run specific test
./gradlew test --tests "D20CombatTest"

# Run tests in package
./gradlew test --tests "com.shatteredpixel.shatteredpixeldungeon.combat.*"

# Clean and rebuild
./gradlew clean build

# Run with verbose output
./gradlew test --info
```

---

## Manual Testing Checklist

### 1. Start New Game
- ✅ Character creation shows Warrior
- ✅ Starting stats: STR 16, DEX 13, CON 14, INT 10, WIS 12, CHA 8
- ✅ Starting HP: 12 (10 + 2 CON mod)
- ✅ Starting equipment: Longsword, Leather armor, Mace (in inventory)

### 2. Hero Info Window
- ✅ All 6 ability scores displayed
- ✅ Ability modifiers shown (+3, +1, +2, +0, +1, -1)
- ✅ AC displayed: 13 (10 base + 1 DEX + 2 leather)
- ✅ BAB displayed: +1

### 3. Combat - Dire Rat
- ✅ Attack with longsword: d20 + 4 (BAB 1 + STR 3) vs AC 15
- ✅ Damage: 1d8 + 3 (longsword + STR)
- ✅ Rat attacks back: d20 + 4 vs your AC 13
- ✅ Rat damage: 1d4
- ✅ Kill rat, gain XP
- ✅ Rat sometimes drops Mystery Meat (33% chance) - raw food

### 4. Combat - Skeleton with Longsword
- ✅ Attack with longsword: d20 + 4 vs AC 15
- ✅ Hit: Damage 1d8+3, but reduced by 5 (DR)
- ✅ Example: Roll 6 damage → only 1 damage to skeleton (6-5=1)
- ✅ If damage ≤ 5, skeleton takes 0 damage
- ✅ This should feel frustrating/ineffective

### 5. Combat - Skeleton with Mace
- ✅ Swap to mace (bludgeoning weapon)
- ✅ Attack with mace: d20 + 4 vs AC 15
- ✅ Hit: Damage 1d8+3, NO REDUCTION (DR bypassed!)
- ✅ Example: Roll 6 damage → full 6 damage to skeleton
- ✅ Skeleton dies much faster with mace
- ✅ Skeleton sometimes drops Dagger (20% chance) - piercing weapon

### 6. Level Up
- ✅ Reach level 2 (after killing ~3-4 enemies)
- ✅ HP increases by 1d10 + 2 (CON mod)
- ✅ BAB increases to +2
- ✅ Attack bonus now: +5 (BAB 2 + STR 3)

### 7. Find Scale Mail
- ✅ Locate scale mail on level 1
- ✅ Equip scale mail
- ✅ AC increases to 15 (10 base + 1 DEX + 4 scale)

### 8. Descend to Level 2
- ✅ Find stairs down
- ✅ Level 2 loads (LastLevel)
- ✅ No enemies spawn

### 9. Victory Condition
- ✅ Find Amulet of Yendor on pedestal
- ✅ Collect amulet
- ✅ Return to entrance stairs
- ✅ Ascend stairs
- ✅ Victory screen appears

---

## Expected Gameplay Experience

### The Learning Curve

**First few encounters (Dire Rats):**
- Player learns d20 combat is swingy
- Sometimes miss 3 times in a row (unlucky d20 rolls)
- Sometimes hit 3 times in a row (lucky rolls)
- AC 15 vs +4 attack = 50% hit chance (need 11+ on d20)

**First skeleton encounter (with Longsword):**
- Player hits skeleton
- Damage message: "8 damage → 3 damage" (DR reduced it)
- Player realizes: "My longsword isn't very effective"
- Skeleton takes many hits to kill

**Discovery moment (switch to Mace):**
- Player opens inventory, equips mace
- Player hits skeleton
- Damage message: "8 damage → 8 damage" (no reduction!)
- Player learns: "Bludgeoning weapons bypass skeleton DR!"

**Strategic choice:**
- Longsword for rats (more damage due to 1d8 > 1d4 enemy HP)
- Mace for skeletons (bypasses DR)
- Weapon switching becomes tactical decision

**Loot system:**
- Rats drop Mystery Meat (33% chance) - raw food for healing
- Skeletons drop Daggers (20% chance) - demonstrates piercing weapons
- Player learns: Different enemies drop thematic loot
- Daggers are weaker (1d4) but demonstrate the 3rd damage type

### D&D Feel

**What feels D&D:**
- ✅ d20 attack rolls (dramatic variance)
- ✅ AC as defense (static, increases with armor)
- ✅ Ability scores and modifiers visible
- ✅ Weapon damage types matter (DR 5/bludgeoning)
- ✅ BAB increases with level
- ✅ Hit dice for HP (d10 for Fighter)

**What doesn't yet:**
- ❌ No saving throws
- ❌ No feats
- ❌ No skills
- ❌ Only 1 class
- ❌ No multiclassing
- ❌ No magic items (yet)
- ❌ No spells (yet)

---

## Success Criteria

### Technical Success
- ✅ All unit tests pass (16+ tests)
- ✅ 40%+ code coverage on core combat
- ✅ Game compiles and runs on Android
- ✅ No crashes during 2-level playthrough

### Gameplay Success
- ✅ d20 combat feels different from original
- ✅ Player notices AC system
- ✅ Player understands ability score modifiers
- ✅ **Player discovers mace is better vs skeletons**
- ✅ Game is winnable in 5-10 minutes
- ✅ Victory condition works (collect amulet)

### Code Quality Success
- ✅ Clean separation of D&D mechanics (Char, Hero)
- ✅ Weapon damage type system extensible
- ✅ Monster DR system works correctly
- ✅ Easy to add more weapons/monsters later

---

## Phase Execution Order (with Hours)

### Week 1-2 (20 hours)
1. ✅ Phase 0: Build setup (2h)
2. ✅ Phase 1: Ability scores (6h)
3. ✅ Phase 2: Fighter init (8h)
4. ✅ Phase 3: D&D combat (4h of 12h)

### Week 3-4 (20 hours)
5. ✅ Phase 3: D&D combat continued (8h)
6. ✅ Phase 4: Weapons (10h)
7. ✅ Phase 5: Armor (2h of 4h)

### Week 5-6 (20 hours)
8. ✅ Phase 5: Armor continued (2h)
9. ✅ Phase 6: Monsters (12h)
10. ✅ Phase 7: Levels (6h of 8h)

### Week 7-8 (20 hours)
11. ✅ Phase 7: Levels continued (2h)
12. ✅ Phase 8: UI (6h)
13. ✅ Phase 9: Starting equipment (2h)
14. ✅ Integration testing (10h)

**PoC Total: 80 hours (2 months at 10 hours/week)**

### Post-PoC (Optional)
15. ⚠️ Phase 10: Project branding & distribution prep (4h) - **Only if preparing for public release**

**Total with Branding: 84 hours**

---

## Post-PoC Expansion Path

Once this ultra-minimal PoC works, expand in this order:

### Iteration 1: More Content (same mechanics)
- Add 3rd level (prison with Guards, Shaman)
- Add 2 more weapons (battleaxe, spear)
- Add 1 more armor (chainmail)
- Add 2 more monsters (Gnoll, Crab)

### Iteration 2: More Classes
- Convert Rogue (DEX-based)
- Convert Mage (INT-based, simple spell system)
- Add subclasses (Berserker, Assassin, Battlemage)

### Iteration 3: D&D Features
- Saving throws (Fort, Ref, Will)
- Skill system (Spot, Search, Disable Device)
- Feat system (Power Attack, Weapon Focus, etc.)

### Iteration 4: Full Game
- All 6 classes with talents
- All 50+ monsters
- All items and equipment
- All 26 levels
- Boss encounters

---

## Files Modified Summary

**PoC Total: 23 files**
**Phase 10 (Branding) Total: +15 files**
**Grand Total with Phase 10: 38 files**

### Core Combat (8 files)
1. Char.java - Abilities, AC, d20 combat
2. Hero.java - Fighter stats, attacks, damage
3. HeroClass.java - Fighter initialization
4. Weapon.java - Damage type system
5. Dagger.java - 1d4 piercing
6. Longsword.java - 1d8 slashing
7. Mace.java - 1d8 bludgeoning
8. Armor.java - AC bonus

### Monsters (2 files)
9. Rat.java - Dire Rat (CR 1/3)
10. Skeleton.java - Skeleton with DR 5/bludgeoning

### Level System (2 files)
11. Dungeon.java - 2-level cap
12. Bestiary.java - Mob spawning

### UI (3 files)
13. WndHero.java - Ability score display
14. StatusPane.java - AC display
15. WelcomeScene.java - D&D note (optional)

### Armor Items (2 files)
16. LeatherArmor.java - Verify tier
17. ScaleArmor.java - Verify tier

### Build (2 files)
18. core/build.gradle - Test dependencies
19. .gitignore - Test output

### Test Files (7 files)
20. CharTest.java
21. HeroTest.java
22. D20CombatTest.java
23. WeaponTest.java
24. ArmorTest.java
25. RatTest.java
26. SkeletonTest.java

### Phase 10: Branding & Distribution (15 files) - *Optional*
27. build.gradle (root) - App name, package, version
28. ShatteredPixelDungeon.java - Version code handling (if needed)
29. banners.png - Title screen graphics
30. android/src/debug/res - Debug icons
31. android/src/main/res - Release icons
32. desktop icons - Desktop application icons
33. iOS icons - iOS assets (if targeting iOS)
34. AboutScene.java - Credits and attribution
35. SupporterScene.java - Supporter links
36. TitleScene.java - Button visibility
37. WndSupportPrompt.java - Support prompt window
38. WornKey.java - Prompt triggers
39. desktop/build.gradle - Update service configuration
40. android/build.gradle - Update service configuration
41. GitHubUpdates.java - Release notifications (optional)
42. ShatteredNews.java - News feed (optional)
43. Languages.java - Translation management
44. messages/*.properties - Translation files
45. WndSettings.java - Language picker

---

## Troubleshooting Common Issues

### Issue: Tests won't compile

**Solution:**
```bash
# Ensure test directory exists
mkdir -p core/src/test/java/com/shatteredpixel/shatteredpixeldungeon

# Verify package names match
# Test package must match source package
```

### Issue: "Cannot resolve symbol Random"

**Solution:**
```java
// Add import
import com.watabou.utils.Random;
```

### Issue: Skeleton takes no damage from mace

**Solution:**
```java
// Debug: Add log in Skeleton.dr()
if (w.getDamageType() == Weapon.DamageType.BLUDGEONING) {
    GLog.i("Bludgeoning weapon! DR bypassed.");
    return 0;
}
```

### Issue: Hero starts with wrong HP

**Solution:**
```java
// In HeroClass.initHero():
hero.HT = hero.HP = 10 + hero.conMod();
// Verify conMod() returns 2 for CON 14
```

### Issue: d20 rolls always miss/hit

**Solution:**
```java
// In Char.hit():
GLog.i("Attack: d20=" + d20 + " bonus=" + attackBonus +
       " total=" + totalAttack + " vs AC=" + defenderAC);
```

---

## Next Steps After Completion

1. **Playtest thoroughly** - Ensure skeleton DR is noticeable
2. **Gather feedback** - Does d20 combat feel D&D-like?
3. **Document lessons learned** - What worked? What didn't?
4. **Phase 10 (Optional)** - Complete project branding if preparing for public distribution
5. **Plan Iteration 1** - Add level 3, more monsters
6. **Consider platform expansion** - Desktop build for easier testing?

---

## Conclusion

This ultra-minimal PoC proves the core D&D 3.5 mechanics (ability scores, d20 combat, AC, BAB, weapon damage types, DR) work in Shattered Pixel Dungeon with only **80 hours of effort**.

**Key validation points:**
- ✅ Ability score system works
- ✅ d20 attack rolls work
- ✅ AC replaces old defense
- ✅ Weapon damage types matter (DR 5/bludgeoning)
- ✅ Game is completable

**What makes this PoC successful:**
- Minimal content (2 monsters, 3 weapons, 2 armors, 2 levels)
- Core D&D mechanics proven
- Foundation for expansion
- Achievable timeline (2 months part-time)
- Optional Phase 10 available for project branding and distribution prep (+4 hours)

Once this works, expanding to full D&D 3.5 with all classes, monsters, and features becomes a matter of content creation rather than system validation.

**Note on Phase 10:** If you plan to publicly distribute your D&D conversion, complete Phase 10 to rebrand the application, update credits, and prepare for distribution. This is optional and should only be done after the PoC succeeds.
