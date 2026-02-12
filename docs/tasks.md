# D&D 3.5 Ultra-Minimal PoC - Implementation Tasks

**Goal:** Convert Shattered Pixel Dungeon to D&D 3.5 core mechanics with minimal content (1 class, 2 monsters, 2 levels)

**Estimated Total Time:** 60-80 hours (1.5-2 months part-time)

---

## Progress Overview

- [ ] Phase 0: Build Setup (2 hours)
- [ ] Phase 1: Ability Scores (6 hours)
- [ ] Phase 2: Fighter Initialization (8 hours)
- [ ] Phase 3: D&D Combat System (12 hours)
- [ ] Phase 4: Weapon System (10 hours)
- [ ] Phase 5: Armor System (4 hours)
- [ ] Phase 6: Monster Conversions (12 hours)
- [ ] Phase 7: Level System (8 hours)
- [ ] Phase 8: UI Updates (6 hours)
- [ ] Phase 9: Starting Equipment (2 hours)
- [ ] Integration Testing & Playthrough (10 hours)
- [ ] Phase 10: Project Branding & Distribution Prep (4 hours) - *Optional, do after PoC success*

---

## Phase 0: Build Setup (2 hours)

### Build Configuration
- [ ] Modify `core/build.gradle` - Add JUnit 4.13.2 and Mockito 4.11.0
- [ ] Add JaCoCo plugin for code coverage
- [ ] Configure test task with logging
- [ ] Add coverage verification (40% minimum)
- [ ] Update `.gitignore` - Add test output directories

### Test Directory Structure
- [ ] Create `core/src/test/java/com/shatteredpixel/shatteredpixeldungeon/`
- [ ] Create `actors/` subdirectory
- [ ] Create `combat/` subdirectory
- [ ] Create `items/weapon/` subdirectory

### Validation
- [ ] Run `./gradlew test` to verify test infrastructure works

---

## Phase 1: Ability Scores (6 hours)

### Core Files
- [ ] **Char.java** - Add 6 ability score fields (STR, DEX, CON, INT, WIS, CHA)
- [ ] **Char.java** - Add modifier methods (strMod(), dexMod(), conMod(), etc.)
- [ ] **Hero.java** - Add ability score fields if needed
- [ ] **Hero.java** - Update storeInBundle() to save abilities
- [ ] **Hero.java** - Update restoreFromBundle() to load abilities

### Tests
- [ ] Create `CharTest.java`
- [ ] Test ability modifier calculation (10-11 = +0, 12-13 = +1, etc.)
- [ ] Test edge cases (3 = -4, 18 = +4)

### Validation
- [ ] All CharTest tests pass
- [ ] Ability modifiers calculate correctly

---

## Phase 2: Fighter Initialization (8 hours)

### Fighter Setup
- [ ] **HeroClass.java** - Set Fighter ability scores (STR 16, DEX 13, CON 14, INT 10, WIS 12, CHA 8)
- [ ] **HeroClass.java** - Set starting HP: 10 + conMod() = 12
- [ ] **HeroClass.java** - Set starting BAB: attackSkill = 1
- [ ] **HeroClass.java** - Update WARRIOR perks description

### Level Up System
- [ ] **Hero.java** - Update onLevelUp() HP gain: 1d10 + conMod()
- [ ] **Hero.java** - Update onLevelUp() BAB increase: attackSkill++

### Tests
- [ ] Create `HeroTest.java`
- [ ] Test Fighter starting stats (all 6 abilities)
- [ ] Test Fighter starting HP (should be 12)
- [ ] Test Fighter level up HP gain
- [ ] Test BAB progression

### Validation
- [ ] All HeroTest tests pass
- [ ] Fighter starts with correct stats

---

## Phase 3: D&D Combat System (12 hours)

### Core Combat Mechanics
- [ ] **Char.java** - Replace hit() method with d20 system
- [ ] **Char.java** - Handle auto-miss on natural 1
- [ ] **Char.java** - Handle auto-hit on natural 20
- [ ] **Char.java** - Implement attack bonus vs AC comparison
- [ ] **Char.java** - Add armorClass() method (base 10 + DEX mod)
- [ ] **Char.java** - Update dr() signature to accept attacker: `dr(Char attacker)`
- [ ] **Char.java** - Update attack() to pass attacker to dr()

### Hero Combat
- [ ] **Hero.java** - Update attackSkill() to return BAB + STR mod (+ weapon bonus)
- [ ] **Hero.java** - Handle ranged weapons using DEX mod
- [ ] **Hero.java** - Update damageRoll() to add STR mod for melee
- [ ] **Hero.java** - Implement minimum 1 damage
- [ ] **Hero.java** - Override armorClass() to include armor AC bonus

### Tests
- [ ] Create `D20CombatTest.java`
- [ ] Test d20 roll range (1-20)
- [ ] Test natural 1 always misses
- [ ] Test natural 20 always hits
- [ ] Test AC calculation (10 + DEX mod)
- [ ] Test attack bonus calculation (BAB + STR)

### Validation
- [ ] All D20CombatTest tests pass
- [ ] Combat uses d20 rolls instead of percentage

---

## Phase 4: Weapon System (10 hours)

### Weapon Base Class
- [ ] **Weapon.java** - Add DamageType enum (SLASHING, PIERCING, BLUDGEONING)
- [ ] **Weapon.java** - Add damageType field
- [ ] **Weapon.java** - Add getDamageType() getter

### Individual Weapons
- [ ] **Dagger.java** - Set min() = 1, max() = 4 (1d4)
- [ ] **Dagger.java** - Set damageType = PIERCING
- [ ] **Longsword.java** - Set min() = 1, max() = 8 (1d8)
- [ ] **Longsword.java** - Set damageType = SLASHING
- [ ] **Mace.java** - Set min() = 1, max() = 8 (1d8)
- [ ] **Mace.java** - Set damageType = BLUDGEONING

### Tests
- [ ] Create `WeaponTest.java`
- [ ] Test Dagger damage range (1-4)
- [ ] Test Longsword damage range (1-8)
- [ ] Test Mace damage range (1-8)
- [ ] Test damage types are set correctly

### Validation
- [ ] All WeaponTest tests pass
- [ ] Weapons have correct damage dice

---

## Phase 5: Armor System (4 hours)

### Armor Base Class
- [ ] **Armor.java** - Add ACBonus() method
- [ ] **Armor.java** - Map tier to AC bonus (tier 2 = +2, tier 3 = +4, etc.)

### Individual Armors
- [ ] **LeatherArmor.java** - Verify tier = 2 (AC +2)
- [ ] **ScaleArmor.java** - Verify tier = 3 (AC +4)

### Tests
- [ ] Create `ArmorTest.java`
- [ ] Test leather armor AC bonus (+2)
- [ ] Test scale armor AC bonus (+4)

### Validation
- [ ] All ArmorTest tests pass
- [ ] Armor contributes to AC correctly

---

## Phase 6: Monster Conversions (12 hours)

### Dire Rat Conversion
- [ ] **Rat.java** - Update name to "dire rat"
- [ ] **Rat.java** - Set HP = HT = 5 (1d8+1)
- [ ] **Rat.java** - Set abilities: STR 10, DEX 17, CON 12, INT 2, WIS 12, CHA 4
- [ ] **Rat.java** - Add armorClass() = 15 (10 + 3 DEX + 1 natural + 1 size)
- [ ] **Rat.java** - Add attackSkill() = BAB 0 + DEX 3 + size 1 = +4
- [ ] **Rat.java** - Update damageRoll() = 1d4 (bite)
- [ ] **Rat.java** - Add dr(Char attacker) = 0 (no DR)
- [ ] **Rat.java** - Add loot drop: MysteryMeat (33% chance)

### Skeleton Conversion
- [ ] **Skeleton.java** - Set HP = HT = 6 (1d12)
- [ ] **Skeleton.java** - Set abilities: STR 13, DEX 13, CON 10, INT 10, WIS 10, CHA 1
- [ ] **Skeleton.java** - Add armorClass() = 15 (10 + 2 natural + 1 DEX + 2 armor)
- [ ] **Skeleton.java** - Add attackSkill() = BAB 0 + STR 1 = +1
- [ ] **Skeleton.java** - Update damageRoll() = 1d4 + STR mod (dagger)
- [ ] **Skeleton.java** - Implement DR 5/bludgeoning mechanic
- [ ] **Skeleton.java** - Check attacker's weapon type
- [ ] **Skeleton.java** - Return 0 DR for bludgeoning weapons
- [ ] **Skeleton.java** - Return 5 DR for non-bludgeoning
- [ ] **Skeleton.java** - Add loot drop: Dagger (20% chance)

### Tests
- [ ] Create `RatTest.java` - Test Dire Rat stats, AC, damage
- [ ] Create `SkeletonTest.java` - Test Skeleton stats and DR
- [ ] Test DR = 5 against slashing weapons
- [ ] Test DR = 0 against bludgeoning weapons
- [ ] Test DR = 0 for unarmed attacks

### Validation
- [ ] All monster tests pass
- [ ] Skeleton DR works correctly

---

## Phase 7: Level System (8 hours)

### Dungeon Configuration
- [ ] **Dungeon.java** - Update newLevel() to cap at depth 2
- [ ] **Dungeon.java** - Depth 1: SewerLevel (combat)
- [ ] **Dungeon.java** - Depth 2: LastLevel (amulet/victory)
- [ ] **Dungeon.java** - Set bossLevel() = false
- [ ] **Dungeon.java** - Set shopOnLevel() = false

### Monster Spawning
- [ ] **Bestiary.java** - Update mobClass() for 2-level system
- [ ] **Bestiary.java** - Depth 1: 60% Rats, 40% Skeletons
- [ ] **Bestiary.java** - Depth 2: No spawns (null)
- [ ] **Bestiary.java** - Set isBoss() = false

### Tests
- [ ] Create `DungeonDepthTest.java` - Test 2-level cap
- [ ] Create `BestiaryTest.java` - Test spawn rates

### Validation
- [ ] Dungeon stops at depth 2
- [ ] Level 1 spawns correct enemies
- [ ] Level 2 has no enemy spawns

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

## Phase 9: Starting Equipment (2 hours)

### Fighter Starting Gear
- [ ] **HeroClass.java** - Give Longsword as starting weapon
- [ ] **HeroClass.java** - Give Leather armor as starting armor
- [ ] **HeroClass.java** - Add Mace to starting inventory
- [ ] **HeroClass.java** - Add standard Food rations

### Validation
- [ ] Start new game as Warrior
- [ ] Verify Longsword equipped
- [ ] Verify Leather armor equipped
- [ ] Verify Mace in inventory
- [ ] Verify Food in inventory

---

## Integration Testing & Manual Playthrough (10 hours)

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
- [ ] Starting equipment: Longsword, Leather armor, Mace (in inventory), Food

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

#### 4. Combat - Skeleton with Longsword
- [ ] Attack with longsword: d20 + 4 vs AC 15
- [ ] Hit: Damage reduced by 5 (DR)
- [ ] Example: Roll 6 damage → only 1 damage to skeleton (6-5=1)
- [ ] If damage ≤ 5, skeleton takes 0 damage
- [ ] Skeleton feels tanky/ineffective to kill

#### 5. Combat - Skeleton with Mace
- [ ] Swap to mace (bludgeoning weapon)
- [ ] Attack with mace: d20 + 4 vs AC 15
- [ ] Hit: Damage NOT reduced (DR bypassed!)
- [ ] Example: Roll 6 damage → full 6 damage to skeleton
- [ ] Skeleton dies much faster with mace

#### 6. Level Up
- [ ] Reach level 2 (after killing ~3-4 enemies)
- [ ] HP increases by 1d10 + 2 (CON mod)
- [ ] BAB increases to +2
- [ ] Attack bonus now: +5 (BAB 2 + STR 3)

#### 7. Find Scale Mail
- [ ] Locate scale mail on level 1 (check if it spawns naturally or needs to be added)
- [ ] Equip scale mail
- [ ] AC increases to 15 (10 base + 1 DEX + 4 scale)

#### 8. Descend to Level 2
- [ ] Find stairs down
- [ ] Level 2 loads (LastLevel)
- [ ] No enemies spawn

#### 9. Victory Condition
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
- [ ] **build.gradle (root)** - Change `appName` to your game's name (e.g., "D&D Pixel Dungeon")
- [ ] **build.gradle (root)** - Change `appPackageName` to format: `com.<yourname>.<gamename>`
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
- [ ] Test on target platforms (Android, Desktop, iOS)
- [ ] Verify GPLv3 compliance (original credits intact, source available)
- [ ] Create GitHub repository for your fork
- [ ] Write README explaining D&D conversion
- [ ] Consider release strategy (alpha, beta, etc.)

---

## Success Criteria

### Technical Success
- [ ] All unit tests pass (16+ tests)
- [ ] 40%+ code coverage on core combat
- [ ] Game compiles and runs on Android
- [ ] No crashes during 2-level playthrough

### Gameplay Success
- [ ] d20 combat feels different from original
- [ ] Player notices AC system
- [ ] Player understands ability score modifiers
- [ ] **Player discovers mace is better vs skeletons**
- [ ] Game is winnable in 5-10 minutes
- [ ] Victory condition works (collect amulet)

### Code Quality Success
- [ ] Clean separation of D&D mechanics (Char, Hero)
- [ ] Weapon damage type system extensible
- [ ] Monster DR system works correctly
- [ ] Easy to add more weapons/monsters later

---

## Files Modified Summary

**PoC Total: 23 files + 7 test files = 30 files**
**Phase 10 (Branding) Total: +15 files = 45 files total if doing distribution**

### Core Combat (8 files)
1. Char.java
2. Hero.java
3. HeroClass.java
4. Weapon.java
5. Dagger.java
6. Longsword.java
7. Mace.java
8. Armor.java

### Monsters (2 files)
9. Rat.java
10. Skeleton.java

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
29. core/src/main/assets/interfaces/banners.png - Title screen
30. android/src/debug/res - Debug icons (multiple files)
31. android/src/main/res - Release icons (multiple files)
32. desktop/src/main/assets/icons - Desktop icons
33. ios/assets/Assets.xcassets - iOS icons
34. AboutScene.java - Credits
35. SupporterScene.java - Supporter links
36. TitleScene.java - Supporter/news buttons
37. WndSupportPrompt.java - Support prompt
38. WornKey.java - Prompt trigger
39. desktop/build.gradle - Update notifications
40. android/build.gradle - Update notifications
41. GitHubUpdates.java - GitHub release URL (optional)
42. ShatteredNews.java - News feed URL (optional)
43. Languages.java - Translation management
44. core/src/main/assets/messages/*.properties - Translation files
45. WndSettings.java - Language picker

---

## Notes & Observations

### Implementation Notes
- (Add notes here as you implement)

### Challenges Encountered
- (Document any unexpected issues)

### Lessons Learned
- (What worked well? What didn't?)

---

## Next Steps After Completion

1. **Thorough Playtesting** - Verify skeleton DR is noticeable
2. **Gather Feedback** - Does d20 combat feel D&D-like?
3. **Document Lessons** - Update this file with findings
4. **Phase 10 (Optional)** - Complete branding if preparing for public distribution
5. **Plan Iteration 1** - Add level 3, more monsters (Gnoll, Crab, Guards)
6. **Consider Expansion** - Desktop build for easier testing?

---

**Last Updated:** 2026-02-12
**Note:** Phase 10 (Project Branding) added - complete ONLY after PoC succeeds and if preparing for distribution.
