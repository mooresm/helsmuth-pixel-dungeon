# D&D 3.5 d20 Combat System Integration (Phase 3)

## Context

You're converting Shattered Pixel Dungeon to use D&D 3.5 combat mechanics. Phase 1-2 added ability scores (STR, DEX, etc.) and Fighter initialization. Phase 3 aims to replace the percentage-based combat system with d20 rolls.

**Current State:**
- AC field exists in Char (line 172), initialized and persisted
- d20Hit() method exists (lines 393-397) but is not called
- Ability scores work correctly
- Hero.attackSkill field serves as BAB (Base Attack Bonus)

**The Problem:**
- Current combat uses `Random.Float(attackSkill)` vs `Random.Float(defenseSkill)` (lines 663-729)
- 75 files use defenseSkill, 63 use attackSkill
- Your new d20Hit() is disconnected from the combat flow
- AC field exists but is not used in combat calculations

**The Goal:**
Replace the hit calculation with d20 + attack bonus vs AC, handling natural 1/20, buffs, and special cases, while minimizing file changes.

---

## Implementation Approach

**Strategy: Use AC Field Directly**

- Use existing AC field instead of creating armorClass() method
- Update AC field when equipment or stats change
- Replace hit() internals to use d20 system with AC field
- Keep attackSkill/defenseSkill method signatures (don't break 75 files)
- Map existing buffs to d20 bonuses (+2, -1, -2)
- Handle INFINITE_ACCURACY/INFINITE_EVASION before rolling

**Files to Modify:** 4 total
1. Char.java - Replace hit() logic to use AC field
2. Hero.java - Update attackSkill(), add updateAC() helper, call updateAC() on equipment changes
3. Rat.java - Set ability scores and AC in initialization
4. Skeleton.java - Set ability scores and AC in initialization

---

## Detailed Implementation Steps

### Step 1: Replace hit() Logic with d20 System

**File:** `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java`

**Method:** `hit(Char attacker, Char defender, float accMulti, boolean magic)` (lines 663-729)

**Replace with:**
```java
public static boolean hit( Char attacker, Char defender, float accMulti, boolean magic ) {
    // Get attack bonus and AC from field
    int attackBonus = attacker.attackSkill(defender);
    int defenderAC = defender.AC;

    // Handle damage interrupts
    if (defender instanceof Hero && ((Hero) defender).damageInterrupt){
        ((Hero) defender).interrupt();
    }

    // Special case: Invisible surprise attacks = auto-hit
    if (attacker.invisible > 0 && attacker.canSurpriseAttack()){
        attackBonus = INFINITE_ACCURACY;
    }

    // Special case: Monk Focus = auto-miss
    if (defender.buff(MonkEnergy.MonkAbility.Focus.FocusBuff.class) != null){
        defenderAC = INFINITE_EVASION;
    }

    // Handle INFINITE_ACCURACY and INFINITE_EVASION
    if (defenderAC >= INFINITE_EVASION){
        hitMissIcon = FloatingText.getMissReasonIcon(attacker, attackBonus, defender, INFINITE_EVASION);
        return false;
    } else if (attackBonus >= INFINITE_ACCURACY){
        hitMissIcon = FloatingText.getHitReasonIcon(attacker, INFINITE_ACCURACY, defender, defenderAC);
        return true;
    }

    // Calculate buff modifiers to attack bonus
    int bonusMod = 0;
    if (attacker.buff(Bless.class) != null) bonusMod += 2;
    if (attacker.buff(Hex.class) != null) bonusMod -= 1;
    if (attacker.buff(Daze.class) != null) bonusMod -= 2;

    for (ChampionEnemy buff : attacker.buffs(ChampionEnemy.class)){
        float factor = buff.evasionAndAccuracyFactor();
        if (factor < 1f) bonusMod -= 2;
        else if (factor > 1f) bonusMod += 1;
    }

    if (AscensionChallenge.statModifier(attacker) < 1f) bonusMod -= 1;
    else if (AscensionChallenge.statModifier(attacker) > 1f) bonusMod += 1;

    if (Dungeon.hero.heroClass != HeroClass.CLERIC
            && Dungeon.hero.hasTalent(Talent.BLESS)
            && attacker.alignment == Alignment.ALLY){
        bonusMod += 1;
    }

    // Calculate buff modifiers to AC
    int acMod = 0;
    if (defender.buff(Bless.class) != null) acMod += 2;
    if (defender.buff(Hex.class) != null) acMod -= 1;
    if (defender.buff(Daze.class) != null) acMod -= 2;

    for (ChampionEnemy buff : defender.buffs(ChampionEnemy.class)){
        float factor = buff.evasionAndAccuracyFactor();
        if (factor < 1f) acMod -= 2;
        else if (factor > 1f) acMod += 1;
    }

    if (AscensionChallenge.statModifier(defender) < 1f) acMod -= 1;
    else if (AscensionChallenge.statModifier(defender) > 1f) acMod += 1;

    if (Dungeon.hero.heroClass != HeroClass.CLERIC
            && Dungeon.hero.hasTalent(Talent.BLESS)
            && defender.alignment == Alignment.ALLY){
        acMod += 1;
    }

    acMod += (int)(FerretTuft.evasionMultiplier() - 1f);

    // Apply accMulti (magic attacks = 2x = +2 bonus)
    if (accMulti > 1f) {
        bonusMod += (int)((accMulti - 1f) * 2);
    } else if (accMulti < 1f) {
        bonusMod -= (int)((1f - accMulti) * 2);
    }

    // Roll d20 and check hit
    int d20Roll = Random.IntRange(1, 20);
    boolean hit = d20Hit(d20Roll, attackBonus + bonusMod, defenderAC + acMod);

    // Debug logging (optional - can be disabled after testing)
    if (Dungeon.level.heroFOV[attacker.pos] || Dungeon.level.heroFOV[defender.pos]) {
        String attackerName = attacker == Dungeon.hero ? "You" : attacker.name();
        String defenderName = defender == Dungeon.hero ? "you" : defender.name();
        int totalBonus = attackBonus + bonusMod;
        int totalAC = defenderAC + acMod;

        GLog.i("%s: d20=%d + %d vs AC %d = %s",
               attackerName, d20Roll, totalBonus, totalAC, hit ? "HIT" : "MISS");
    }

    // Set hit/miss icon
    if (hit) {
        hitMissIcon = FloatingText.getHitReasonIcon(attacker, d20Roll + attackBonus + bonusMod,
                                                     defender, defenderAC + acMod);
    } else {
        hitMissIcon = FloatingText.getMissReasonIcon(attacker, d20Roll + attackBonus + bonusMod,
                                                      defender, defenderAC + acMod);
    }

    return hit;
}
```

**Buff Mappings:**
- Bless (1.25x) → +2 bonus
- Hex (0.8x) → -1 penalty
- Daze (0.5x) → -2 penalty
- Magic attacks (2x accMulti) → +2 bonus

**Debug Logging:**
The code above includes optional debug logging that displays:
- `"You: d20=15 + 4 vs AC 15 = HIT"` (when hero attacks)
- `"Rat: d20=12 + 4 vs AC 13 = HIT"` (when mob attacks)

This helps verify d20 mechanics are working correctly. The logging:
- Only shows for visible combats (within hero's FOV)
- Uses `GLog.i()` for informational messages (white text)
- Can be commented out after testing if too verbose

**Available GLog methods:**
- `GLog.i()` - Informational (white) - use for debug info
- `GLog.p()` - Positive (green, ++ prefix) - successes/buffs
- `GLog.n()` - Negative (red, -- prefix) - failures/deaths
- `GLog.w()` - Warning (orange, ** prefix) - alerts/debuffs
- `GLog.h()` - Highlight (@@ prefix) - emphasized messages

---

### Step 2: Update Hero.attackSkill() and Add updateAC() Helper

**File:** `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java`

**Part A - Update attackSkill() method (line 504):**

**Replace with simplified version (for PoC):**
```java
@Override
public int attackSkill( Char target ) {
    // BAB (Base Attack Bonus) = attackSkill field
    int baseAttackBonus = this.attackSkill;

    // Ability modifier: STR for melee, DEX for ranged
    KindOfWeapon wep = belongings.attackingWeapon();
    int abilityMod;
    if (wep instanceof MissileWeapon) {
        abilityMod = statBonus(DEX);
    } else {
        abilityMod = statBonus(STR);
    }

    // Total = BAB + ability mod
    return Math.max(1, baseAttackBonus + abilityMod);
}
```

**Note:** Existing weapon accuracy factors and ring bonuses will be handled by accMulti parameter and buff modifiers in hit().

**Part B - Add updateAC() helper method:**

**Location:** After defenseSkill() method (~line 620)

**Add:**
```java
/**
 * Updates AC field based on current DEX and equipped armor.
 * Call this when equipment changes or DEX changes.
 */
public void updateAC() {
    AC = 10 + statBonus(DEX);

    if (belongings.armor != null) {
        // Armor tier → AC bonus: tier 2 = +2, tier 3 = +4, tier 4 = +6, tier 5 = +8
        AC += belongings.armor.tier * 2;
    }
}
```

**Part C - Call updateAC() on initialization:**

In Hero constructor or after initial equipment is set, add:
```java
updateAC(); // Calculate initial AC
```

**Part D - Call updateAC() when equipment changes:**

You'll need to call `updateAC()` in methods where armor is equipped/unequipped. For the PoC, we can manually test or add hooks later. The key places are:
- When armor is equipped via item use
- When armor is removed
- When DEX changes (rare, but can happen with curses)

---

### Step 3: Implement Rat (Dire Rat) D&D Stats

**File:** `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Rat.java`

**Update initialization block (~line 33):**
```java
{
    spriteClass = RatSprite.class;

    HP = HT = 8;
    defenseSkill = 2; // Keep for compatibility

    // D&D Dire Rat ability scores
    STR = 10;
    DEX = 17; // +3 modifier
    CON = 12;
    INT = 2;
    WIS = 12;
    CHA = 4;

    // D&D AC: 10 base + 3 DEX + 1 natural + 1 size = 15
    AC = 10 + statBonus(DEX) + 1 + 1;

    maxLvl = 5;
}
```

**Update attackSkill() (~line 60):**
```java
@Override
public int attackSkill( Char target ) {
    // BAB 0 + DEX +3 + size +1 = +4
    return 0 + statBonus(DEX) + 1;
}
```

---

### Step 4: Implement Skeleton D&D Stats

**File:** `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Skeleton.java`

**Update initialization block (~line 49):**
```java
{
    spriteClass = SkeletonSprite.class;

    HP = HT = 25;
    defenseSkill = 9; // Keep for compatibility

    // D&D Skeleton ability scores
    STR = 13; // +1 modifier
    DEX = 13; // +1 modifier
    CON = 10;
    INT = 10;
    WIS = 10;
    CHA = 1;

    // D&D AC: 10 base + 1 DEX + 2 natural + 2 armor = 15
    AC = 10 + statBonus(DEX) + 2 + 2;

    EXP = 5;
    maxLvl = 10;

    loot = Generator.Category.WEAPON;
    lootChance = 0.1667f;

    properties.add(Property.UNDEAD);
    properties.add(Property.INORGANIC);
}
```

**Update attackSkill() (~line 157):**
```java
@Override
public int attackSkill( Char target ) {
    // BAB 0 + STR +1 = +1
    return 0 + statBonus(STR);
}
```

---

## Optional: Enhanced Debug Logging

For easier debugging during development, you can add more detailed combat logging:

**Option A: Verbose Combat Log (Temporary)**

Add this at the end of the hit() method (after the existing debug log):
```java
// Additional detail logging for development
if (bonusMod != 0 || acMod != 0) {
    GLog.i("  Modifiers: attack %+d, AC %+d", bonusMod, acMod);
}
if (d20Roll == 1) {
    GLog.w("  Natural 1 - automatic miss!");
} else if (d20Roll == 20) {
    GLog.p("  Natural 20 - automatic hit!");
}
```

**Option B: Conditional Debug Flag**

Add a static debug flag that can be toggled:
```java
// In Char.java, add near the top
public static boolean DEBUG_COMBAT = true;  // Set to false to disable

// In hit() method, wrap debug logs:
if (DEBUG_COMBAT && (Dungeon.level.heroFOV[attacker.pos] || Dungeon.level.heroFOV[defender.pos])) {
    // ... logging code ...
}
```

**Option C: No Debug Logging**

Skip the debug logging entirely and rely on:
- FloatingText hit/miss icons (already implemented)
- Unit tests to verify mechanics
- Manual playtesting

**Recommendation:** Use Option A during initial implementation, then switch to Option B or C once the system is stable.

---

## Critical Files

1. `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/Char.java` - Replace hit() to use AC field
2. `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java` - Update attackSkill(), add updateAC() helper
3. `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Rat.java` - Set ability scores and AC
4. `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/Skeleton.java` - Set ability scores and AC

---

## Verification Strategy

### Unit Tests
The D20CombatTest.java file already has tests for natural 1/20. Add these tests to verify AC field values:

```java
@Test
public void testRatAC() {
    Rat rat = new Rat();
    assertEquals("Rat AC should be 15", 15, rat.AC);
}

@Test
public void testSkeletonAC() {
    Skeleton skeleton = new Skeleton();
    assertEquals("Skeleton AC should be 15", 15, skeleton.AC);
}

@Test
public void testHeroACCalculation() {
    Hero hero = new Hero();
    hero.DEX = 13; // +1 modifier
    hero.updateAC();
    assertEquals("Hero base AC (no armor) should be 11", 11, hero.AC);
}
```

Run: `./gradlew test --tests "D20CombatTest"`

### Manual Testing Checklist

1. **Start game as Warrior**
   - Verify starting AC: 13 (10 + 1 DEX + 2 leather armor)
   - Verify attack bonus: +4 (BAB 1 + STR 3)

2. **Attack a rat**
   - Hit on d20 roll of 11+ (need 15 total: d20 + 4 vs AC 15)
   - Verify combat log shows different patterns than old system

3. **Get attacked by rat**
   - Rat hits you on d20 roll of 9+ (need 13 total: d20 + 4 vs AC 13)
   - Verify rat damage rolls occur

4. **Attack skeleton**
   - Same threshold as rat (AC 15)
   - Verify skeleton takes damage (DR tested in Phase 6)

5. **Test Bless buff**
   - Cast Bless on self
   - Verify improved hit rate (approximately +10% = +2 bonus)

6. **Test Hex debuff**
   - Get hexed by enemy
   - Verify reduced hit rate (approximately -5% = -1 penalty)

7. **Level up to 2**
   - Verify BAB increases to 2
   - New attack bonus: +5 (BAB 2 + STR 3)
   - Now hit rat/skeleton on d20 roll of 10+ (15 - 5 = 10)

---

## Key Design Decisions

### Why Keep attackSkill/defenseSkill?
- **Backward compatibility**: 75+ files call these methods
- **No functional impact**: They return values but hit() no longer uses defenseSkill
- **Future cleanup**: Can deprecate after PoC succeeds

### Why Map Buffs to Flat Bonuses?
- **Simplicity**: D&D uses flat bonuses, not multipliers
- **Balance**: +2 for Bless ≈ 10% improvement on d20 scale
- **Extensibility**: Easy to adjust if too strong/weak

### Why Not Implement Advantage/Disadvantage?
- **Scope**: PoC goal is minimal proof of concept
- **Complexity**: Requires roll-twice logic, UI changes
- **Future**: Can add in Phase 8+ if needed

### Why Only 4 Files?
- **Rat and Skeleton**: Only mobs needed for Phase 3
- **All other mobs**: Will inherit AC = 0 initially (gets replaced in Phase 6)
- **Hero**: Needs updateAC() helper to recalculate when equipment changes
- **Phase 6**: Will add Kobold and Tiny Viper by setting AC in initialization

---

## Success Criteria

**Technical:**
- ✅ All D20CombatTest tests pass
- ✅ Game compiles and runs without crashes
- ✅ No errors in hit() method

**Gameplay:**
- ✅ Fighter needs d20 roll of 11+ to hit rat/skeleton (AC 15)
- ✅ Rat hits fighter on roll of 9+ (AC 13)
- ✅ Combat feels different from old percentage system
- ✅ Natural 1 always misses, natural 20 always hits
- ✅ Bless improves hit rate noticeably

**Code Quality:**
- ✅ Only 4 files modified (not 75+)
- ✅ AC field pattern easy to extend to more mobs (just set AC in initialization)
- ✅ Buffs continue to work with new mappings
- ✅ Simpler architecture using existing AC field
