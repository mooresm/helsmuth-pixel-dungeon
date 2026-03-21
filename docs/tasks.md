# D&D 3.5 Ultra-Minimal PoC - Implementation Tasks

**Goal:** Convert Shattered Pixel Dungeon to D&D 3.5 core mechanics with minimal content (1 class, 4 monsters, 2 levels)

**Estimated Total Time:** 60-80 hours (1.5-2 months part-time)

---

## Progress Overview

- [x] Phase 0: Build Setup (2 hours)
- [x] Phase 1: Ability Scores (6 hours)
- [x] Phase 2: Fighter Initialization (8 hours)
- [x] Phase 3: D&D Combat System (12 hours)
- [x] Phase 4: Weapon System (10 hours)
- [x] Phase 5: Armor System (4 hours)
- [x] Phase 6: Monster Conversions (12 hours)
- [x] Phase 7: Level System (8 hours)
- [ ] Phase 8: UI Updates (6 hours)
- [ ] Phase 9: Integration Testing & Playthrough (10 hours)
- [ ] Phase 10: Project Branding & Distribution Prep (4 hours) - *Optional, do after PoC success*

---

## Phase 0: Build Setup (2 hours)

### Build Configuration
- [x] Modify `core/build.gradle` - Add JUnit 4.13.2 and Mockito (upgraded to mockito-inline:5.2.0 for Java 21 support)
- [x] Add JaCoCo plugin for code coverage
- [x] Configure test task with logging
- [x] Add coverage verification (40% minimum)
- [x] Update `.gitignore` - Add test output directories

### Test Directory Structure
- [x] Create `core/src/test/java/com/shatteredpixel/shatteredpixeldungeon/`
- [x] Create `actors/hero` subdirectory
- [x] Create minimal `HeroTest` for `STR()` getter

### Validation
- [x] Run `./gradlew test` to verify test infrastructure works

---

## Phase 1: Ability Scores (6 hours)

### Core Files
- [x] **Char.java** - Add 6 ability score fields (STR, DEX, CON, INT, WIS, CHA)
- [x] **Char.java** - Add static modifier method statBonus(int stat)
- [x] **Hero.java** - Update storeInBundle() to save abilities
- [x] **Hero.java** - Update restoreFromBundle() to load abilities

### Tests
- [x] Create `CharTest.java`
- [x] Test ability modifier calculation (10-11 = +0, 12-13 = +1, etc.)
- [x] Test edge cases (3 = -4, 18 = +4)

### Validation
- [x] All CharTest tests pass
- [x] Ability modifiers calculate correctly

---

## Phase 2: Fighter Initialization (8 hours)

### Fighter Setup
- [x] **HeroClass.java** - Set Fighter ability scores (STR 16, DEX 13, CON 14, INT 10, WIS 12, CHA 8)
- [x] **HeroClass.java** - Set starting HP: 10 + conMod() = 12
- [x] **HeroClass.java** - Set starting BAB: attackSkill = 1
- [x] **HeroClass.java** - Update WARRIOR perks description

### Level Up System
- [x] **Hero.java** - Update onLevelUp() HP gain: 1d10 + conMod()

### Tests
- [x] Create `HeroTest.java`
- [x] Test Hero default field values (STR, DEX, CON, INT, WIS, CHA)
- [x] Test ability score field assignment and statBonus() modifiers
- [ ] ~~Test Fighter starting stats via initHero()~~ **NOT UNIT TESTABLE** — `initHero()` constructs
  item objects whose constructors load LibGDX sprite sheets (`ItemSpriteSheet$Icons.<clinit>`),
  which requires a running GPU/file context. These are integration tests, not unit tests.
  Verify Fighter starting stats manually by running the game instead.
- [x] Test Fighter starting HP (should be 12) — **manual test only** (same reason)
- [x] Test Fighter level up HP gain — **manual test only**

> **Testing boundary:** Any code path that calls `new SomeItem()` or `item.identify()` will
> trigger LibGDX static initializers and cannot be unit tested without a running engine.
> Pure field/calculation methods on `Char`, `Hero`, and mob classes are safe to unit test.

### Validation
- [x] All HeroTest tests pass (4 tests: default values, field assignment, modifier calculation)
- [x] Fighter ability scores set correctly in HeroClass.java (verified by code review)
- [ ] Fighter starts with correct stats (manual game test)

---

## Phase 3: D&D Combat System (12 hours)

### Core Combat Mechanics
- [x] **Char.java** - Replace hit() method with d20 system
- [x] **Char.java** - Handle auto-miss on natural 1
- [x] **Char.java** - Handle auto-hit on natural 20
- [x] **Char.java** - Implement attack bonus vs AC comparison
- [x] **Char.java** - Add new AC field (base 10 + DEX mod)
- [x] **Char.java** - Update dr() signature to accept attacker: `dr(Char attacker)`
- [x] **Char.java** - Update attack() to pass attacker to dr()

### Hero Combat
- [x] **Hero.java** - Update attackSkill() to return BAB + STR mod (+ weapon bonus)
- [x] **Hero.java** - Handle ranged weapons using DEX mod
- [x] **Hero.java** - Call updateAC() in constructor/initialization
- [x] **Hero.java** - Update damageRoll() to add STR mod for melee
- [x] **Hero.java** - Implement minimum 1 damage
- [x] **Hero.java** - Override armorClass() to include armor AC bonus

### Tests
- [x] Create `D20CombatTest.java`
- [x] Test natural 1 always misses
- [x] Test natural 20 always hits
- [x] Test Rat AC field = 15
- [x] Test Skeleton AC field = 13
- [x] Test Hero updateAC() calculation
- [x] Test attack bonus calculation (BAB + STR)
  **Note:** Test these as pure calculations on `Char` fields/methods — do not
  instantiate items or call `initHero()` inside combat tests (see Phase 2 testing boundary)

### Validation
- [x] All D20CombatTest tests pass
- [x] Combat uses d20 rolls instead of percentage
- [x] Hero AC updates when equipment changes

---

## Phase 4: Weapon System (10 hours)

### Weapon Base Class
- [x] **Weapon.java** - Add DamageType enum (SLASHING, PIERCING, BLUDGEONING)
- [x] **Weapon.java** - Add damageType field
- [x] **Weapon.java** - Add getDamageType() getter

### Individual Weapons
- [x] **Dagger.java** - Set min() = 1, max() = 4 (1d4)
- [x] **Dagger.java** - Set damageType = PIERCING
- [x] **Longsword.java** - Set min() = 1, max() = 8 (1d8)
- [x] **Longsword.java** - Set damageType = SLASHING
- [x] **Cudgel.java** - Set min() = 1, max() = 6 (1d6)
- [x] **Cudgel.java** - Set damageType = BLUDGEONING
- [x] **Mace.java** - Set min() = 1, max() = 8 (1d8)
- [x] **Mace.java** - Set damageType = BLUDGEONING

### Tests
- [x] Create `WeaponTest.java`
- [x] Test Dagger damage range (1-4)
- [x] Test Longsword damage range (1-8)
- [x] Test Club damage range (1-6)
- [x] Test Mace damage range (1-8)
- [x] Test damage types are set correctly
**Note:** Instantiating weapon objects (`new Dagger()` etc.) will trigger
  `ItemSpriteSheet$Icons.<clinit>` — same LibGDX boundary as Phase 2. Test
  `damageType` and damage ranges by setting fields directly on a `Weapon` subclass
  instance, or extract damage/type as static constants that can be tested without
  constructing the full item object.

### Validation
- [x] All WeaponTest tests pass
- [x] Weapons have correct damage dice

---

## Phase 5: Armor System (4 hours)

### Armor Base Class
- [x] **Armor.java** - Add ACBonus() method
- [x] **Armor.java** - Map tier to AC bonus (tier 1 = +2, tier 3 = +4, etc.)
- [x] adjust to-hit, damage and AC based on magical enhancement bonus

---

## Phase 6: Monster Conversions (18 hours)

### Dire Rat Conversion
- [x] **Rat.java** - Update name to "dire rat"
- [x] **Rat.java** - Set HP = HT = 5 (1d8+1)
- [x] **Rat.java** - Set abilities: STR 10, DEX 17, CON 12, INT 2, WIS 12, CHA 4
- [x] **Rat.java** - Add armorClass() = 15 (10 + 3 DEX + 1 natural + 1 size)
- [x] **Rat.java** - Add attackSkill() = BAB 0 + DEX 3 + size 1 = +4
- [x] **Rat.java** - Update damageRoll() = 1d4 (bite)
- [x] **Rat.java** - Add loot drop: MysteryMeat (33% chance)

### Skeleton Conversion
- [x] **Skeleton.java** - Set HP = HT = 6 (1d12)
- [x] **Skeleton.java** - Set abilities: STR 13, DEX 13, CON 10, INT 10, WIS 10, CHA 1
- [x] **Skeleton.java** - Add armorClass() = 15 (10 + 2 natural + 1 DEX + 2 armor)
- [x] **Skeleton.java** - Add attackSkill() = BAB 0 + STR 1 = +1
- [x] **Skeleton.java** - Update damageRoll() = 1d4 + STR mod (dagger)
- [x] **Skeleton.java** - Implement DR 5/bludgeoning mechanic
- [x] **Skeleton.java** - Check attacker's weapon type
- [x] **Skeleton.java** - Return 0 DR for bludgeoning weapons
- [x] **Skeleton.java** - Return 5 DR for non-bludgeoning
- [x] **Skeleton.java** - Print "Your weapon glances off the bones!" in game log when DR negates all damage

### Kobold Conversion
- [x] **Kobold.java** (new file, or repurpose Gnoll.java) - Set name to "kobold"
- [x] **Kobold.java** - Set HP = HT = 4 (1d8)
- [x] **Kobold.java** - Set abilities: STR 9, DEX 13, CON 10, INT 10, WIS 9, CHA 8
- [x] **Kobold.java** - Add armorClass() = 15 (10 + 1 DEX + 1 natural + 1 armor + 2 size)
- [x] **Kobold.java** - Add attackSkill() = BAB 0 + DEX 1 = +1
- [x] **Kobold.java** - Update damageRoll() = 1d3 (dagger, STR -1 penalty, min 1)
- [x] **Kobold.java** - Add loot drop: Dagger (15% chance), Gold (25% chance)

### Tiny Viper Conversion
- [x] **Snake.java** (or new TinyViper.java) - Set name to "tiny viper"
- [x] **Snake.java** - Set HP = HT = 2 (1d4)
- [x] **Snake.java** - Set abilities: STR 4, DEX 17, CON 11, INT 1, WIS 12, CHA 2
- [x] **Snake.java** - Add armorClass() = 17 (10 + 3 DEX + 2 natural + 2 size)
- [x] **Snake.java** - Add attackSkill() = +5 (finesse, size bonus)
- [x] **Snake.java** - Update damageRoll() = 1 (bite, STR -3 penalty, min 1)
- [ ] **Snake.java** - Implement simplified poison: on hit, apply Poison buff (1 extra damage next turn)
- [ ] **Snake.java** - Print "You feel the venom!" in game log when poison is applied
- [x] **Snake.java** - Add loot drop: Antidote (20% chance)

### Tests
- [x] Mob stats and combat tested in `D20CombatTest.java`
- [x] Skeleton DR = 5 vs slashing, DR = 0 vs bludgeoning tested in `D20CombatTest.java`
- [x] Kobold minimum damage = 1 tested in `D20CombatTest.java`
- [ ] Tiny Viper poison buff tested in `D20CombatTest.java`
- [ ] Mob XP awards tested in `CRExperienceTest.java`
- [x] Test viper damage minimum = 1
- [ ] Test Antidote drops at 20% chance

### Validation
- [ ] All monster tests pass
- [x] Skeleton DR works correctly
- [x] Kobold deals minimum 1 damage despite STR penalty
- [ ] Tiny Viper poison buff applies correctly

---

## Phase 7: Level System (8 hours)

### Dungeon Configuration
- [x] **Dungeon.java** - Update newLevel() to cap at depth 5
- [x] **Dungeon.java** - Depths 1-4: SewerLevel (combat)
- [x] **Dungeon.java** - Depth 5: LastLevel (amulet/victory)
- [x] **Dungeon.java** - Set shopOnLevel() = true at level 4

### Tests
- [x] Create `DungeonDepthTest.java` - Test 2-level cap

### Validation
- [x] Dungeon stops at depth 5
- [x] Levels 1-4 spawn correct enemies
- [x] Level 4 has a shop
- [x] Level 5 has no enemy spawns

---

## Phase 7b: CR-Based Experience System (3 hours)

### Background: D&D 3.5 Challenge Rating (CR)

In D&D 3.5, monsters have a Challenge Rating that determines XP awards:
- **CR 1/8**: 50 XP (level 1), 25 XP (level 2)
- **CR 1/4**: 75 XP (level 1), 38 XP (level 2)
- **CR 1/3**: 100 XP (level 1), 50 XP (level 2)
- **CR 1/2**: 150 XP (level 1), 100 XP (level 2)
- **CR 1**: 300 XP (level 1), 200 XP (level 2)

XP awards scale based on the difference between party level and monster CR.

### Implementation Strategy

**Use existing EXP field to store CR as a float multiplier:**
- Rat (CR 1/3): `EXP = 0.33f`
- Skeleton (CR 1/3): `EXP = 0.33f`
- Kobold (CR 1/4): `EXP = 0.25f`
- Tiny Viper (CR 1/8): `EXP = 0.125f`

**Create CR-to-XP conversion table in Hero or Experience helper class.**

### Core XP System Changes

- [ ] **Hero.java** - Add getCRExperience(float cr, int heroLevel) method
- [ ] **Hero.java** - Create XP table for CR to XP conversion:
  - Level 1 vs CR 1/8: 50 XP
  - Level 1 vs CR 1/4: 75 XP
  - Level 1 vs CR 1/3: 100 XP
  - Level 1 vs CR 1/2: 150 XP
  - Level 1 vs CR 1: 300 XP
  - Level 2 vs CR 1/8: 25 XP
  - Level 2 vs CR 1/4: 38 XP
  - Level 2 vs CR 1/3: 50 XP
  - Level 2 vs CR 1/2: 100 XP
  - Level 2 vs CR 1: 200 XP
- [ ] **Hero.java** - Update earnExp(int exp) to use getCRExperience() when EXP < 1.0
- [ ] **Mob.java** - Add comment explaining EXP field now stores CR for D&D mobs

### Monster CR Configuration

- [ ] **Rat.java** - Change `EXP = 5` to `EXP = 0.33f` (CR 1/3)
- [ ] **Skeleton.java** - Change `EXP = 5` to `EXP = 0.33f` (CR 1/3)
- [ ] **Kobold.java** - Set `EXP = 0.25f` (CR 1/4)
- [ ] **Snake.java** (Tiny Viper) - Set `EXP = 0.125f` (CR 1/8)

### XP Calculation Logic

**Current system (in Mob.java or Hero.java):**
```java
// On mob death, awards mob.EXP directly
hero.earnExp(mob.EXP);
```

**New system:**
```java
// On mob death, check if EXP is a CR value (< 1.0)
if (mob.EXP < 1.0f) {
    // Treat as CR, calculate XP based on hero level
    int xp = hero.getCRExperience(mob.EXP, hero.lvl);
    hero.earnExp(xp);
} else {
    // Legacy mob, use EXP value directly
    hero.earnExp((int)mob.EXP);
}
```

### Tests

- [ ] Create `CRExperienceTest.java`
- [ ] Test CR 1/3 awards 100 XP to level 1 hero
- [ ] Test CR 1/3 awards 50 XP to level 2 hero
- [ ] Test CR 1/4 awards 75 XP to level 1 hero
- [ ] Test CR 1/8 awards 50 XP to level 1 hero
- [ ] Test legacy mobs (EXP >= 1.0) still work

### Validation

- [ ] Kill rat at level 1, verify 100 XP awarded
- [ ] Kill rat at level 2, verify 50 XP awarded
- [ ] Kill skeleton at level 1, verify 100 XP awarded
- [ ] Verify level 2 is reached after ~12 rat kills (1200 XP needed)
- [ ] XP display shows correct values in combat log

### Notes

- **Simplified D&D system**: Only implements level 1-2 XP table
- **CR as float**: Allows fractional CR values (1/8 = 0.125, 1/3 = 0.333, etc.)
- **Backward compatible**: Mobs with EXP >= 1.0 use old system
- **Full D&D XP tables**: Can be added later for levels 3-20

---

## Phase 8: UI Updates (6 hours)

### Hero Stats Window
- [ ] **WndHero.java** - Add STR display with modifier
- [ ] **WndHero.java** - Add DEX display with modifier
- [ ] **WndHero.java** - Add CON display with modifier
- [ ] **WndHero.java** - Add INT display with modifier
- [ ] **WndHero.java** - Add WIS display with modifier
- [ ] **WndHero.java** - Add CHA display with modifier
- [ ] **WndHero.java** - Add AC display
- [ ] **WndHero.java** - Add BAB display

### Status Display
- [ ] **StatusPane.java** - Add AC display (replace old defense if needed)

### Welcome Message (Optional)
- [ ] **WelcomeScene.java** - Add note about D&D conversion

### Validation
- [ ] Manual testing: Open hero window, verify all stats visible
- [ ] Manual testing: AC shows in status pane

---

## Phase 9: Integration Testing & Manual Playthrough (10 hours)

### Unit Test Suite
- [ ] Run all tests: `./gradlew test`
- [ ] Generate coverage report: `./gradlew jacocoTestReport`
- [ ] Verify 40%+ code coverage
- [ ] Fix any failing tests

### Manual Testing Checklist

#### 1. Start New Game
- [ ] Character creation shows Warrior
- [ ] Starting stats: STR 16, DEX 13, CON 14, INT 10, WIS 12, CHA 8
- [ ] Starting HP: 12 (10 + 2 CON mod)
- [ ] Starting equipment: Longsword, Leather Armor, 1x Healing Potion, Food

#### 2. Hero Info Window
- [ ] All 6 ability scores displayed
- [ ] Ability modifiers shown (+3, +1, +2, +0, +1, -1)
- [ ] AC displayed: 13 (10 base + 1 DEX + 2 leather)
- [ ] BAB displayed: +1

#### 3. Combat - Dire Rat
- [ ] Attack with longsword: d20 + 4 (BAB 1 + STR 3) vs AC 15
- [ ] Damage: 1d8 + 3 (longsword + STR)
- [ ] Rat attacks back: d20 + 4 vs your AC 13
- [ ] Rat damage: 1d4
- [ ] Kill rat, gain XP
- [ ] Rat sometimes drops Mystery Meat (33% chance)

#### 4. Combat - Kobold
- [ ] Attack with longsword: d20 + 4 vs AC 15
- [ ] Kobold attacks back: d20 + 1 vs your AC 13
- [ ] Kobold damage: 1 (minimum, STR penalty)
- [ ] Kill kobold, check for Gold or Dagger drop

#### 5. Combat - Tiny Viper
- [ ] Viper attacks: d20 + 5 vs your AC 13
- [ ] On hit: "You feel the venom!" appears in game log
- [ ] Poison deals 1 extra damage next turn
- [ ] Viper dies quickly (2 HP)
- [ ] Viper sometimes drops Antidote (20% chance)
- [ ] Test using Antidote clears poison buff

#### 6. Combat - Skeleton with Longsword
- [ ] Attack with longsword: d20 + 4 vs AC 15
- [ ] Hit: Damage reduced by 5 (DR)
- [ ] Example: Roll 6 damage → only 1 damage to skeleton (6-5=1)
- [ ] If damage ≤ 5: "Your weapon glances off the bones!" in game log, 0 damage
- [ ] Skeleton feels extremely tanky without bludgeoning weapon

#### 7. Combat - Skeleton with Club (found via exploration)
- [ ] Find Club in chest or room drop
- [ ] Swap to Club (bludgeoning)
- [ ] Attack with club: d20 + 4 vs AC 15
- [ ] Hit: Damage NOT reduced (DR bypassed!)
- [ ] Skeleton dies much faster

#### 8. Level Up
- [ ] Reach level 2 (after killing ~3-4 enemies)
- [ ] HP increases by 1d10 + 2 (CON mod)
- [ ] BAB increases to +2
- [ ] Attack bonus now: +5 (BAB 2 + STR 3)

#### 9. Find Scale Mail
- [ ] Locate scale mail on level 1 (check if it spawns naturally or needs to be added)
- [ ] Equip scale mail
- [ ] AC increases to 15 (10 base + 1 DEX + 4 scale)

#### 10. Descend to Level 2
- [ ] Find stairs down
- [ ] Level 2 loads (LastLevel)
- [ ] No enemies spawn

#### 11. Victory Condition
- [ ] Find Amulet of Yendor on pedestal
- [ ] Collect amulet
- [ ] Return to entrance stairs
- [ ] Ascend stairs
- [ ] Victory screen appears

### Bug Fixes
- [ ] Document any bugs found during playthrough
- [ ] Fix critical bugs
- [ ] Re-test after fixes

---

## Phase 10: Project Branding & Distribution Prep (4 hours)

**Note:** This phase is OPTIONAL and should only be done AFTER the PoC is successful. These changes prepare your project for public distribution as a separate fork of Shattered Pixel Dungeon.

### Application Identity (1 hour)

#### Update Build Configuration
- [x] **build.gradle (root)** - Change `appName` to your game's name (e.g., "D&D Pixel Dungeon")
- [x] **build.gradle (root)** - Change `appPackageName` to format: `com.<yourname>.<gamename>`
- [ ] **build.gradle (root)** - Update `appVersionName` to your version (e.g., "0.1.0-alpha")
- [ ] **build.gradle (root)** - Keep `appVersionCode` at Shattered's current value or higher (don't decrement)

#### Version Code Considerations
- [ ] Review ShatteredPixelDungeon.java constants if you want to reset version code to 1
- [ ] Find all version-based compatibility code if changing version code
- [ ] Document decision on version code strategy

### Visual Identity (1.5 hours)

#### Title Screen
- [ ] **core/src/main/assets/interfaces/banners.png** - Replace title graphic
- [ ] **core/src/main/assets/interfaces/banners.png** - Replace glow layer for title

#### Application Icons
- [ ] **android/src/debug/res** - Replace Android debug icons (multiple sizes)
- [ ] **android/src/main/res** - Replace Android release icons (multiple sizes)
- [ ] **desktop/src/main/assets/icons** - Replace desktop icons
- [ ] **ios/assets/Assets.xcassets** - Replace iOS icons (if targeting iOS)

### Credits & Attribution (0.5 hours)

#### Update About Scene
- [ ] **AboutScene.java** - Add yourself to credits
- [ ] **AboutScene.java** - Maintain existing Shattered credits (GPLv3 requirement)
- [ ] **AboutScene.java** - Add link to your project repository

#### Supporter Button
- [ ] **SupporterScene.java** - Update supporter links to your own (if desired)
- [ ] **TitleScene.java** - OR disable supporter button by commenting out `add(btnSupport);`
- [ ] **WndSupportPrompt.java** - Edit or disable first-victory nag window
- [ ] **WornKey.java** - Disable prompt trigger if unwanted
- [ ] **Note:** If releasing on Google Play, disable/modify Patreon references (Google policy)

### Update Notifications (0.5 hours)

#### Disable Original Shattered Notifications
- [ ] **desktop/build.gradle** - Change `:services:updates:githubUpdates` to `:services:updates:debugUpdates`
- [ ] **android/build.gradle** - Change `:services:updates:githubUpdates` to `:services:updates:debugUpdates`

#### OR Configure Your Own GitHub Releases (Optional)
- [ ] **GitHubUpdates.java** - Change URL to your GitHub repo: `https://api.github.com/repos/<yourname>/<yourrepo>/releases`
- [ ] Create GitHub releases with format: title, body with `---`, and `internal version number: #`

### News Feed (0.25 hours)

#### Disable Shattered News
- [ ] **TitleScene.java** - Comment out `add(btnNews);` to remove news button

#### OR Configure Your Own News Feed (Optional)
- [ ] **ShatteredNews.java** - Modify URLs to point to your atom/xml feed
- [ ] Test feed parsing with your feed format
- [ ] Adjust parsing logic if needed

### Translations (0.25 hours)

#### Decision: Keep or Remove Translations?
- [ ] Decide if maintaining community translations is feasible
- [ ] Consider if you're adding new text that won't be translated

#### If Removing All Non-English Languages:
- [ ] **Languages.java** - Remove all enum constants except ENGLISH
- [ ] **core/src/main/assets/messages/** - Remove all `*_XX.properties` files (keep base `*.properties`)
- [ ] **WndSettings.java** - Comment out `add( langs );` and `add( langsTab );`

#### If Keeping Some Languages:
- [ ] Remove only unwanted language enums from Languages.java
- [ ] Remove only unwanted `*_XX.properties` files
- [ ] Keep language picker in WndSettings.java

#### If Changing Base Language (Advanced):
- [ ] Remove base .properties files (no language code)
- [ ] Rename your language files to remove underscore+code
- [ ] Update ENGLISH enum references throughout codebase

### Validation
- [ ] Build compiles successfully with new branding
- [ ] App launches with new name and icon
- [ ] Credits display correctly
- [ ] Update notifications don't point to Shattered's repo
- [ ] News feed disabled or points to your feed
- [ ] Language settings appropriate for your translation strategy

### Distribution Checklist
- [x] Test on target platforms (Android, Desktop, iOS)
- [ ] Verify GPLv3 compliance (original credits intact, source available)
- [x] Create GitHub repository for your fork
- [x] Write README explaining D&D conversion
- [ ] Consider release strategy (alpha, beta, etc.)

---

## Success Criteria

### Technical Success
- [ ] All unit tests pass (22+ tests)
- [ ] 40%+ code coverage on core combat
- [ ] Game compiles and runs on Android
- [ ] No crashes during 2-level playthrough

### Gameplay Success
- [ ] d20 combat feels different from original
- [ ] Player notices AC system
- [ ] Player understands ability score modifiers
- [ ] **Player discovers club/mace is better vs skeletons**
- [ ] **"Glances off the bones!" message appears when hitting skeleton without bludgeoning weapon**
- [ ] **Tiny Viper poison provides "oh no" moment without being unfair**
- [ ] Game is winnable in 5-10 minutes
- [ ] Victory condition works (collect amulet)

### Code Quality Success
- [ ] Clean separation of D&D mechanics (Char, Hero)
- [ ] Weapon damage type system extensible
- [ ] Monster DR system works correctly
- [ ] Easy to add more weapons/monsters later

---

## Files Modified Summary

**PoC Total: 28 files + 6 test files = 34 files**
**Phase 10 (Branding) Total: +15 files = 49 files total if doing distribution**

### Core Combat (8 files)
1. Char.java
2. Hero.java
3. HeroClass.java
4. Weapon.java
5. Dagger.java
6. Longsword.java
7. Mace.java
8. Armor.java

### Monsters (4 files)
9. Rat.java
10. Skeleton.java
11. Kobold.java (new, or repurposed Gnoll.java)
12. Snake.java / TinyViper.java

### Level System (2 files)
11. Dungeon.java
12. Bestiary.java

### UI (3 files)
13. WndHero.java
14. StatusPane.java
15. WelcomeScene.java (optional)

### Armor Items (2 files)
16. LeatherArmor.java
17. ScaleArmor.java

### Build (2 files)
18. core/build.gradle
19. .gitignore

### Test Files (6 files)
20. CharTest.java
21. HeroTest.java
22. D20CombatTest.java - Includes mob combat and DR testing
23. WeaponTest.java
24. ArmorTest.java
25. CRExperienceTest.java - Includes mob XP testing

### Phase 10: Branding & Distribution (15 files) - *Optional*
26. build.gradle (root) - App name, package, version
27. ShatteredPixelDungeon.java - Version code handling (if needed)
28. core/src/main/assets/interfaces/banners.png - Title screen
29. android/src/debug/res - Debug icons (multiple files)
30. android/src/main/res - Release icons (multiple files)
31. desktop/src/main/assets/icons - Desktop icons
32. ios/assets/Assets.xcassets - iOS icons
33. AboutScene.java - Credits
34. SupporterScene.java - Supporter links
35. TitleScene.java - Supporter/news buttons
36. WndSupportPrompt.java - Support prompt
37. WornKey.java - Prompt trigger
38. desktop/build.gradle - Update notifications
39. android/build.gradle - Update notifications
40. GitHubUpdates.java - GitHub release URL (optional)
41. ShatteredNews.java - News feed URL (optional)
42. Languages.java - Translation management
43. core/src/main/assets/messages/*.properties - Translation files
44. WndSettings.java - Language picker

---

## Phase 11: Complete Monster Conversion (Post-PoC Expansion)

**Goal:** Convert all remaining Shattered Pixel Dungeon mobs to D&D 3.5 equivalents with appropriate stats, abilities, and Challenge Ratings.

**Note:** This phase is for full game conversion after the PoC is complete. Check off mobs as they are converted.

### Sewers (Depths 1-5)

**Basic Enemies:**
- [x] Rat.java → **Dire Rat** (CR 1/3) - PoC complete
- [x] Snake.java → **Tiny Viper** (CR 1/8) - Planned in PoC
- [x] Gnoll.java → **Kobold** (CR 1/4) - Planned in PoC, or use actual Gnoll (CR 1)
- [x] Crab.java → **Giant Crab** (CR 2)
- [x] Swarm.java → **Rat Swarm** (CR 2)

**Variants:**
- [ ] FetidRat.java → **Dire Rat** variant with disease (CR 1/2)
- [ ] Albino.java → **Albino Dire Rat** variant (CR 1/2)

**Slimes & Oozes:**
- [ ] CausticSlime.java → **Gray Ooze** (CR 4)
- [x] Slime.java → **Gelatinous Cube** (CR 3)

**Boss:**
- [ ] Goo.java → **Black Pudding** (CR 7)

### Prison (Depths 6-10)

**Humanoids:**
- [x] Skeleton.java → **Human Skeleton** (CR 1/3) - PoC complete
- [ ] Thief.java → **Human Rogue** 2nd level (CR 1)
- [ ] Bandit.java → **Human Warrior** 1st level (CR 1)
- [ ] Guard.java → **Human Fighter** 3rd-5th level (CR 2-4)

**Magic Users:**
- [ ] Shaman.java (Gnoll) → **Gnoll Adept** 2nd level (CR 2)
- [ ] Necromancer.java → **Human Necromancer** 5th-9th level (CR 5-9)
- [ ] SpectralNecromancer.java → **Ghost Necromancer** (CR 8-10)

**Undead:**
- [ ] Ghoul.java → **Ghoul** (CR 1)
- [ ] Wraith.java → **Wraith** (CR 5)
- [ ] TormentedSpirit.java → **Specter** (CR 7)

**Boss:**
- [ ] Tengu.java → **Ninja/Assassin Boss** (CR 7-10)

### Caves (Depths 11-15)

**Orcs & Brutes:**
- [ ] Brute.java → **Orc Warrior** 2nd level or Bugbear (CR 2-3)
- [ ] ArmoredBrute.java → **Orc Fighter** 3rd level with scale mail (CR 3-4)

**Spiders & Spinners:**
- [ ] Spinner.java → **Monstrous Spider** Medium (CR 1)
- [ ] FungalSpinner.java → **Fungal Spider** (CR 2)

**Flying:**
- [ ] Bat.java → **Bat Swarm** (CR 2)

**Gnoll Variants:**
- [ ] GnollExile.java → **Gnoll** basic (CR 1)
- [ ] GnollGeomancer.java → **Gnoll Adept** (earth/stone magic) (CR 2)
- [ ] GnollGuard.java → **Gnoll Warrior** with shield (CR 1-2)
- [ ] GnollSapper.java → **Gnoll Saboteur** (explosives) (CR 2)
- [ ] GnollTrickster.java → **Gnoll Rogue** 2nd level (CR 2)

**Shaman:**
- [ ] Shaman.java (Cave) → **Orc Adept** 3rd level (CR 3)

**Constructs:**
- [ ] DM100.java → **Small Construct** (CR 2)
- [ ] DM200.java → **Medium Construct** (CR 4-5)
- [ ] DM201.java → **Advanced Construct** (CR 6-7)

**Boss:**
- [ ] DM300.java → **Large Construct** (CR 10)

### Dwarven Metropolis (Depths 16-20)

**Monks:**
- [ ] Monk.java → **Human Monk** 3rd-6th level (CR 3-6)
- [ ] Senior.java → **Human Monk** 7th-10th level (CR 7-10)

**Elementals:**
- [ ] Elemental.java → **Fire Elemental** (cold halls) or **Ice Elemental** (burning halls) (CR 5-9)
- [ ] CrystalWisp.java → **Will-o'-Wisp** or Small Elemental (CR 4)

**Constructs & Golems:**
- [ ] Golem.java → **Stone Golem** (CR 11)
- [x] Statue.java → **Animated Statue** (CR 3)
- [ ] ArmoredStatue.java → **Animated Armor** (CR 4-6)

**Magic Users:**
- [ ] Warlock.java → **Human Warlock** 6th-9th level (CR 6-9)

**Crystal Creatures:**
- [ ] CrystalGuardian.java → **Crystal Golem** (CR 5-7)
- [ ] CrystalSpire.java → **Immobile Crystal Defender** (CR 4-6)

**Boss:**
- [ ] DwarfKing.java → **Dwarf Fighter/King** 12th-15th level (CR 12-15)

### Demon Halls (Depths 21-25)

**Demons:**
- [ ] Succubus.java → **Succubus** (CR 7)
- [ ] RipperDemon.java → **Vrock** or similar Demon (CR 9-12)
- [ ] DemonSpawner.java → **Demon Summoner** (immobile) (CR 8-10)

**Aberrations:**
- [ ] Eye.java → **Beholder** (simplified) or **Evil Eye** (CR 10-13)
- [ ] Scorpio.java → **Monstrous Scorpion** Huge (CR 8) - Boss variant

**Boss:**
- [ ] YogDzewa.java → **Demon Lord** or **Elder Evil** (CR 20+)
- [ ] YogFist.java → **Demon Fist** (summoned) (CR 10-15) - 6 types (Burning, Soiled, Rotting, Rusted, Bright, Dark)

### Special & Quest Monsters

**Mimics:**
- [x] Mimic.java → **Mimic** (CR 4)
- [x] GoldenMimic.java extends Mimic
- [x] CrystalMimic.java → **Crystal Mimic** (CR 7)
- [x] EbonyMimic.java → **Ebony Mimic** (CR 8)

**Aquatic:**
- [x] Piranha.java → **Quipper** or Small Piranha (CR 1/2)
- [ ] PhantomPiranha.java → **Ethereal Quipper** (CR 2)

**Crabs:**
- [ ] GreatCrab.java → **Giant Crab** Large (CR 3)
- [ ] HermitCrab.java → **Hermit Crab** (defensive) (CR 2)

**Rot Garden:**
- [ ] RotHeart.java → **Shambling Mound** or Plant Creature (CR 5-7)
- [ ] RotLasher.java → **Vine Horror** (CR 3)

**Fungal:**
- [ ] FungalCore.java → **Fungal Creature** (CR 5)
- [ ] FungalSentry.java → **Myconid Guard** (CR 3)

**Traps & Hazards:**
- [ ] DelayedRockFall.java → **Trap/Hazard** (not a creature, assign CR to trap)
- [ ] Pylon.java → **Magical Defense Device** (CR varies by depth)

**Other:**
- [ ] Bee.java → **Giant Bee** (CR 1/2)
- [ ] VaultRat.java → **Dire Rat** variant (guardian) (CR 1/2)
- [ ] Acidic.java → **Acid-spitting creature** (CR 1-2)

### Mob Conversion Checklist Summary

**Total Mobs:** 74 regular mobs + 5 bosses = **79 mobs**
- [x] **Converted:** 2 (Rat, Skeleton)
- [x] **Planned in PoC:** 2 (Snake/Tiny Viper, Gnoll/Kobold)
- [ ] **Remaining:** 75 mobs

**Conversion Progress:** 2/79 (3%)

### Conversion Guidelines

For each mob conversion:
1. **Research D&D equivalent** - Use Monster Manual, d20srd.org
2. **Set ability scores** - STR, DEX, CON, INT, WIS, CHA
3. **Calculate AC** - 10 + DEX mod + natural armor + armor bonus
4. **Set HP** - Hit dice appropriate to CR (e.g., 2d8+2 for CR 1)
5. **Set attack bonus** - BAB + STR/DEX mod
6. **Set damage** - Weapon damage + STR mod
7. **Special abilities** - Translate SPD abilities to D&D (e.g., poison, DR, regeneration)
8. **Set CR and XP** - Use EXP field as CR (e.g., 0.5f for CR 1/2, 1.0f for CR 1, 2.0f for CR 2)
9. **Test in game** - Verify balance and difficulty
10. **Check off in list** - Mark [x] when complete

---

## Notes & Observations

### Implementation Notes
- Ability scores added to `Char.java` as public int fields; `statBonus()` added as static method
- `HeroClass.initWarrior()` sets D&D Fighter scores (STR 16, DEX 13, CON 14, INT 10, WIS 12, CHA 8)
- Mockito upgraded from `mockito-core:4.11.0` to `mockito-inline:5.2.0` — required for Java 21
  support (Byte Buddy 1.14+) and `MockedStatic`. Note: `mockito-inline` is JVM-only and cannot
  run as Android instrumented tests.

### Challenges Encountered
- **LibGDX unit test boundary:** `initHero()` and any code that calls `new SomeItem()` or
  `item.identify()` is not unit-testable without a running LibGDX context. The call chain
  `Item.identify() → Catalog.setSeen() → Badges.validateCatalogBadges() → Document.<clinit>
  → DeviceCompat.isDebug() → Game.version (null)` causes `ExceptionInInitializerError`.
  Even after fixing `Game.version`, `ScrollOfIdentify.<init>` triggers
  `ItemSpriteSheet$Icons.<clinit>` which tries to load GPU textures via `Gdx.files`.
  **Resolution:** Unit test only pure field/calculation code. Verify `initHero()` behaviour
  manually by running the game.
- **QuickSlot cannot be mocked:** `QuickSlot` is Android-compiled bytecode; Byte Buddy's
  inline instrumentation cannot rewrite it on the JVM. Use `new QuickSlot()` instead.
- **Static initializer poisoning:** A failed `<clinit>` marks the class as permanently broken
  for the entire JVM run. All subsequent tests get `NoClassDefFoundError` for that class
  even if the root cause is fixed in a later test. Always fix static initializer failures
  before the first test that triggers them (`setUp()` is the right place).

### Lessons Learned
- Keep unit tests focused on pure logic (field values, calculations, comparisons). SPD's item
  and sprite systems are tightly coupled and require integration-level testing via the game itself.
- The testable boundary in this codebase is: `Char` fields and static utility methods, mob stat
  fields set directly, damage type enums, DR calculation logic. Everything that touches `Item`,
  `Dungeon`, or LibGDX rendering is integration territory.
- For future phases, design new D&D mechanics as pure methods on `Char` or mob classes first,
  then wire them into items/sprites separately. This keeps the core logic unit-testable.

---

## Next Steps After Completion

1. **Thorough Playtesting** - Verify skeleton DR is noticeable
2. **Gather Feedback** - Does d20 combat feel D&D-like?
3. **Document Lessons** - Update this file with findings
4. **Phase 10 (Optional)** - Complete branding if preparing for public distribution
5. **Plan Iteration 1** - Add level 3, more monsters (Gnoll, Crab, Guards)
6. **Consider Expansion** - Desktop build for easier testing?

---

**Last Updated:** 2026-03-04
**Note:** Phase 10 (Project Branding) added - complete ONLY after PoC succeeds and if preparing for distribution.
