package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EbonyMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snake;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.watabou.utils.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TDD tests for the D&D 3.5 d20 combat system.
 *
 * These tests define the contract for the NEW hit() implementation before it
 * is written. All tests operate on pure fields/calculations — no LibGDX
 * context, no Dungeon, no buffs, no sprites.
 *
 * The new hit() must delegate its pure d20 logic to a package-private helper:
 *
 *   static boolean d20Hit(int d20Roll, int attackBonus, int defenderAC)
 *
 * This is the only way to test the logic without fighting the LibGDX boundary
 * (see Phase 2 notes: buff(), Dungeon.hero, FloatingText all poison the JVM).
 *
 * Implementation contract enforced by these tests:
 *   - Roll 1d20 (1–20 inclusive)
 *   - Natural 1  → always miss, regardless of bonuses
 *   - Natural 20 → always hit, regardless of AC
 *   - Otherwise  → hit if (d20 + attackBonus) >= defenderAC
 *
 * armorClass() contract:
 *   - Base Char: 10 + dexMod()
 */
public class D20CombatTest {

    @Before
    public void setUp() {
        Random.pushGenerator(42L);
    }

    @After
    public void tearDown() {
        Random.popGenerator();
    }

    // ==================== NATURAL 1: AUTO-MISS ====================

    /**
     * A natural 1 is a fumble — always misses even if the attacker's bonus
     * would exceed the defender's AC.
     */
    @Test
    public void testNatural1AlwaysMisses() {
        // Even with a +100 attack bonus vs AC 1, a natural 1 must miss
        assertFalse("Natural 1 must always miss",
                Char.d20Hit(1, 100, 1));
    }

    @Test
    public void testNatural1MissesAgainstLowestAC() {
        assertFalse("Natural 1 misses even vs AC 2 (lowest non-trivial AC)",
                Char.d20Hit(1, 20, 2));
    }

    // ==================== NATURAL 20: AUTO-HIT ====================

    /**
     * A natural 20 is a critical — always hits even against impenetrable AC.
     */
    @Test
    public void testNatural20AlwaysHits() {
        // Even with -100 attack bonus vs AC 1000, a natural 20 must hit
        assertTrue("Natural 20 must always hit",
                Char.d20Hit(20, -100, 1000));
    }

    @Test
    public void testNatural20HitsAgainstHighestReasonableAC() {
        // Scale mail skeleton: AC 15. Natural 20 beats it regardless of bonus.
        assertTrue("Natural 20 hits against AC 15",
                Char.d20Hit(20, -5, 15));
    }

    // ==================== NORMAL HIT/MISS RESOLUTION ====================

    /**
     * Core rule: hit if (d20 + attackBonus) >= defenderAC.
     */
    @Test
    public void testHitsWhenTotalEqualsAC() {
        // d20=10, bonus=+3, total=13 vs AC=13 → hit (>= is a hit)
        assertTrue("Total attack equal to AC should hit",
                Char.d20Hit(10, 3, 13));
    }

    @Test
    public void testHitsWhenTotalExceedsAC() {
        // d20=15, bonus=+3, total=18 vs AC=13 → hit
        assertTrue("Total attack exceeding AC should hit",
                Char.d20Hit(15, 3, 13));
    }

    @Test
    public void testMissesWhenTotalBelowAC() {
        // d20=5, bonus=+3, total=8 vs AC=13 → miss
        assertFalse("Total attack below AC should miss",
                Char.d20Hit(5, 3, 13));
    }

    @Test
    public void testMissesByOne() {
        // d20=9, bonus=+3, total=12 vs AC=13 → miss (one short)
        assertFalse("Total attack one below AC should miss",
                Char.d20Hit(9, 3, 13));
    }

    @Test
    public void testHitsByOne() {
        // d20=10, bonus=+3, total=13 vs AC=13 → hit (exact)
        // d20=11, bonus=+2, total=13 vs AC=13 → also hit
        assertTrue("Total attack equal to AC by one should hit",
                Char.d20Hit(11, 2, 13));
    }

    // ==================== ATTACK BONUS CASES ====================

    @Test
    public void testNegativeAttackBonusCanStillHit() {
        // Low-level mob with -1 BAB rolling a 14 vs AC 10: 14 + (-1) = 13 >= 10 → hit
        assertTrue("Negative attack bonus can still result in a hit on high roll",
                Char.d20Hit(14, -1, 10));
    }

    @Test
    public void testNegativeAttackBonusMisses() {
        // d20=5, bonus=-1, total=4 vs AC 10 → miss
        assertFalse("Negative attack bonus causes miss on low roll",
                Char.d20Hit(5, -1, 10));
    }

    @Test
    public void testZeroAttackBonus() {
        // Untrained combatant: BAB 0, STR 10 (mod 0) → pure d20 vs AC
        assertTrue("Zero bonus: roll=12 vs AC=12 should hit",
                Char.d20Hit(12, 0, 12));
        assertFalse("Zero bonus: roll=11 vs AC=12 should miss",
                Char.d20Hit(11, 0, 12));
    }

    // ==================== D&D FIGHTER VS TYPICAL MONSTERS ====================

    /**
     * Fighter (BAB 1, STR 16 = +3 bonus, total +4) attacking a Dire Rat (AC 15).
     * Needs d20 roll of 11+ to hit: 11 + 4 = 15 >= 15.
     */
    @Test
    public void testFighterHitsDireRatOnElevenOrHigher() {
        int fighterAttackBonus = 4; // BAB 1 + STR mod +3
        int direRatAC = 15;

        // Roll of 11 is the minimum to hit
        assertTrue("Fighter hits Dire Rat on roll of 11",
                Char.d20Hit(11, fighterAttackBonus, direRatAC));

        // Roll of 10 misses: 10 + 4 = 14 < 15
        assertFalse("Fighter misses Dire Rat on roll of 10",
                Char.d20Hit(10, fighterAttackBonus, direRatAC));
    }

    /**
     * Fighter (attack bonus +4) attacking a Skeleton (AC 15).
     * Same threshold as Dire Rat — confirms AC symmetry.
     */
    @Test
    public void testFighterHitsSkeletonOnElevenOrHigher() {
        int fighterAttackBonus = 4;
        int skeletonAC = 15;

        assertTrue("Fighter hits Skeleton on roll of 11",
                Char.d20Hit(11, fighterAttackBonus, skeletonAC));
        assertFalse("Fighter misses Skeleton on roll of 10",
                Char.d20Hit(10, fighterAttackBonus, skeletonAC));
    }

    /**
     * Kobold (BAB 0, DEX 13 = +1, size +1, total +2) attacking Fighter (AC 14 with leather).
     * Needs a 12 to hit: 12 + 2 = 14 >= 14.
     */
    @Test
    public void testKoboldHitsFighterInLeatherArmor() {
        int koboldAttackBonus = 2; // BAB 0 + DEX mod +1 + size +1
        int fighterACInLeather = 14; // 10 + DEX mod +1 (DEX 13) + leather +2 = 13... rounded: AC 13
        // Using AC 14 as a round number to test the formula clearly

        assertTrue("Kobold hits armored Fighter on roll of 12",
                Char.d20Hit(12, koboldAttackBonus, fighterACInLeather));
        assertFalse("Kobold misses armored Fighter on roll of 11",
                Char.d20Hit(11, koboldAttackBonus, fighterACInLeather));
    }

    @Test
    public void testRat() {
        Rat rat = new Rat();
        assertEquals("Rat AC should be 15", 15, rat.AC);
        assertTrue("Rat HP >= 2", rat.HP >= 2);
        assertTrue("Rat HP <= 9", rat.HP <= 9);
        assertEquals("Rat should have +4 to hit", 4, rat.attackSkill(rat));
        assertTrue("damage >= 1", rat.damageRoll() >= 1);
        assertTrue("damage <= 4", rat.damageRoll() <= 4);
    }

    @Test
    public void testSkeleton() {
        Skeleton skeleton = new Skeleton();
        assertEquals("Skeleton AC should be 13", 13, skeleton.AC);
        assertTrue("Skeleton HP >= 1", skeleton.HP >= 1);
        assertTrue("Skeleton HP <= 12", skeleton.HP <= 12);
        assertEquals("Skeleton should have +1 to hit", 1, skeleton.attackSkill(skeleton));
        assertTrue("damage >= 2", skeleton.damageRoll() >= 2);
        assertTrue("damage <= 5", skeleton.damageRoll() <= 5);
    }

    @Test
    public void testSnake() {
        Snake snake = new Snake();
        assertEquals("Snake AC should be 17", 17, snake.AC);
        assertTrue("Snake HP >= 1", snake.HP >= 1);
        assertTrue("Snake HP <= 8", snake.HP <= 8);
        assertEquals("Snake should have +4 to hit", 4, snake.attackSkill(snake));
        assertTrue("damage >= 1", snake.damageRoll() >= 1);
        assertTrue("damage <= 4", snake.damageRoll() <= 4);
    }

    @Test
    public void testGnoll() {
        Gnoll kobold = new Gnoll();
        assertEquals("Kobold AC should be 15", 15, kobold.AC);
        assertTrue("Kobold HP >= 1", kobold.HP >= 1);
        assertTrue("Kobold HP <= 8", kobold.HP <= 8);
        assertEquals("Kobold should have +1 to hit", 1, kobold.attackSkill(kobold));
        assertTrue("damage >= 0", kobold.damageRoll() >= 0);
        assertTrue("damage <= 5", kobold.damageRoll() <= 5);
    }

    @Test
    public void testPiranha() {
        Piranha barracuda = new Piranha();
        assertEquals("barracuda AC should be 14", 14, barracuda.AC);
        assertTrue("barracuda HP >= 2", barracuda.HP >= 2);
        assertTrue("barracuda HP <= 16", barracuda.HP <= 16);
        assertEquals("barracuda should have +4 to hit", 4, barracuda.attackSkill(barracuda));
        assertTrue("damage >= 1", barracuda.damageRoll() >= 1);
        assertTrue("damage <= 4", barracuda.damageRoll() <= 4);
    }

    @Test
    public void testCrab() {
        Crab crab = new Crab();
        assertEquals("Crab AC should be 16", 16, crab.AC);
        assertTrue("Crab HP >= 9", crab.HP >= 9);
        assertTrue("Crab HP <= 30", crab.HP <= 30);
        assertEquals("Crab should have +4 to hit", 4, crab.attackSkill(crab));
        assertTrue("damage >= 3", crab.damageRoll() >= 3);
        assertTrue("damage <= 8", crab.damageRoll() <= 8);
    }

    @Test
    public void testFly() {
        Swarm fly = new Swarm();
        assertEquals("Giant fly AC should be 14", 14, fly.AC);
        assertTrue("Giant fly HP >= 3", fly.HP >= 3);
        assertTrue("Giant fly HP <= 24", fly.HP <= 24);
        assertEquals("Giant fly should have +2 to hit", 2, fly.attackSkill(fly));
        assertTrue("damage >= 1", fly.damageRoll() >= 1);
        assertTrue("damage <= 4", fly.damageRoll() <= 4);
    }

    @Test
    public void testMimic() {
        Mimic mimic = new Mimic();
        assertEquals("Mimic AC should be 15", 15, mimic.AC);
        assertTrue("Mimic HP >= 28", mimic.HP >= 28);
        assertTrue("Mimic HP <= 77", mimic.HP <= 77);
        assertEquals("Mimic alignment neutral", Char.Alignment.NEUTRAL, mimic.alignment);
        assertEquals("Flat footed, so +10 to hit", 10, mimic.attackSkill(mimic));
        // now switch to attack mode
        mimic.alignment = Char.Alignment.ENEMY;
        assertEquals("Mimic should now have +9 to hit", 9, mimic.attackSkill(mimic));
        assertTrue("damage >= 5", mimic.damageRoll() >= 5);
        assertTrue("damage <= 12", mimic.damageRoll() <= 12);
    }

    @Test
    public void testGoldenMimic() {
        GoldenMimic mimic = new GoldenMimic();
        assertEquals("Golden mimic AC should be 16", 16, mimic.AC);
        assertTrue("Golden mimic HP >= 72", mimic.HP >= 72);
        assertTrue("Golden mimic HP <= 156", mimic.HP <= 156);
        assertEquals("Golden mimic alignment neutral", Char.Alignment.NEUTRAL, mimic.alignment);
        assertEquals("Flat footed, so +16 to hit", 16, mimic.attackSkill(mimic));
        // now switch to attack mode
        mimic.alignment = Char.Alignment.ENEMY;
        assertEquals("Golden mimic should now have +16 to hit", 16, mimic.attackSkill(mimic));
        assertTrue("damage >= 11", mimic.damageRoll() >= 11);
        assertTrue("damage <= 21", mimic.damageRoll() <= 21);
    }

    @Test
    public void testCrystalMimic() {
        CrystalMimic mimic = new CrystalMimic();
        assertEquals("Crystal Mimic AC should be 16", 16, mimic.AC);
        assertTrue("Crystal Mimic HP >= 96", mimic.HP >= 96);
        assertTrue("Crystal Mimic HP <= 208", mimic.HP <= 208);
        assertEquals("Crystal Mimic alignment neutral", Char.Alignment.NEUTRAL, mimic.alignment);
        assertEquals("Flat footed, so +20 to hit", 20, mimic.attackSkill(mimic));
        // now switch to attack mode
        mimic.alignment = Char.Alignment.ENEMY;
        assertEquals("Crystal Mimic should now have +20 to hit", 20, mimic.attackSkill(mimic));
        assertTrue("damage >= 12", mimic.damageRoll() >= 12);
        assertTrue("damage <= 22", mimic.damageRoll() <= 22);
    }

    @Test
    public void testEbonyMimic() {
        EbonyMimic mimic = new EbonyMimic();
        assertEquals("Ebony Mimic AC should be 16", 16, mimic.AC);
        assertTrue("Ebony mimic HP >= 72", mimic.HP >= 72);
        assertTrue("Ebony mimic HP <= 156", mimic.HP <= 156);
        assertEquals("Ebony Mimic alignment neutral", Char.Alignment.NEUTRAL, mimic.alignment);
        assertEquals("Flat footed, so +23 to hit", 23, mimic.attackSkill(mimic));
        // now switch to attack mode
        mimic.alignment = Char.Alignment.ENEMY;
        assertEquals("Ebony Mimic should now have +23 to hit", 23, mimic.attackSkill(mimic));
        assertTrue("damage >= 12", mimic.damageRoll() >= 12);
        assertTrue("damage <= 22", mimic.damageRoll() <= 22);
    }

    @Test
    public void testSlime() {
        Slime slime = new Slime();
        assertEquals("Slime AC should be 4", 4, slime.AC);
        assertTrue("Slime HP >= 36", slime.HP >= 36);
        assertTrue("Slime HP <= 72", slime.HP <= 72);
        assertEquals("Slime should have +2 to hit", 2, slime.attackSkill(slime));
        assertTrue("damage >= 1", slime.damageRoll() >= 1);
        assertTrue("damage <= 6", slime.damageRoll() <= 6);
    }

    // ==================== BLACK PUDDING (Goo) ====================
    @Test
    public void testGoo() {
        Goo pudding = new Goo();
        assertEquals("Black Pudding AC should be 3", 3, pudding.AC);
        // HP = 10d10+60, average 115; stronger bosses not active in tests (Dungeon.challenges = 0)
        assertTrue("Black Pudding HP >= 70",  pudding.HP >= 70);
        assertTrue("Black Pudding HP <= 160", pudding.HP <= 160);
        assertEquals("Black Pudding should have +8 to hit (healthy)", 8, pudding.attackSkill(pudding));
        // Damage: 2d6+4, range 6–16
        assertTrue("damage >= 6",  pudding.damageRoll() >= 6);
        assertTrue("damage <= 16", pudding.damageRoll() <= 16);
    }

    @Test
    public void testHeroACCalculation() {
        Hero hero = new Hero();
        hero.DEX = 13; // +1 modifier
        hero.updateAC();
        assertEquals("Hero base AC (no armor) should be 11", 11, hero.AC);
    }

    // ==================== Hero::attackSkill() ====================

    /**
     * Unarmed (no weapon): BAB + STR mod, minimum 1.
     * With Fighter's STR 16 (+3) and BAB 1 → returns 4.
     */
    @Test
    public void testFighterAttackSkillUnarmed() {
        Hero hero = new Hero();
        hero.STR = 16; // +3 mod
        hero.attackSkill = 1; // BAB
        assertEquals("Fighter unarmed attack skill: BAB 1 + STR mod 3 = 4",
                4, hero.attackSkill(hero));
    }

    /**
     * Weak STR (-1 mod) + low BAB: minimum 1 still applies.
     */
    @Test
    public void testAttackSkillMinimum() {
        Hero hero = new Hero();
        hero.STR = 8; // -1 mod
        hero.attackSkill = 0; // BAB 0, total = -1 → clamped to 1
        assertEquals("Negative total attack skill should clamp to 1",
                1, hero.attackSkill(hero));
    }

    /**
     * High STR, BAB 0: ability mod alone drives the bonus.
     */
    @Test
    public void testAttackSkillStrModOnlyNoBab() {
        Hero hero = new Hero();
        hero.STR = 18; // +4 mod
        hero.attackSkill = 0;
        assertEquals("STR 18 with no BAB: 0 + 4 = 4",
                4, hero.attackSkill(hero));
    }

    /**
     * Level-up increases BAB: attackSkill field increments each level.
     * Fighter BAB 2 at level 2, STR 16 (+3) → 5.
     */
    @Test
    public void testFighterAttackSkillAtLevel2() {
        Hero hero = new Hero();
        hero.STR = 16; // +3 mod
        hero.attackSkill = 2; // BAB after level-up
        assertEquals("Fighter level 2: BAB 2 + STR mod 3 = 5",
                5, hero.attackSkill(hero));
    }

// ==================== updateAC(): Shield bonus ====================

/**
 * RoundShield in the weapon slot adds +1 shield bonus (+ upgrade level).
 * Tested by setting belongings.weapon to a real RoundShield instance.
 *
 * NOTE: RoundShield() triggers ItemSpriteSheet.<clinit> — same LibGDX
 * boundary as other item constructors. If this test causes
 * ExceptionInInitializerError, move it to a manual integration test
 * and verify in-game instead (same resolution as initHero() tests).
 */
@Test
public void testShieldAddsOneACToBase() {
    Hero hero = new Hero();
    hero.DEX = 10; // +0 mod → base AC = 10
    hero.updateAC();
    int baseAC = hero.AC;
    assertEquals("Base AC with DEX 10, no armor, no shield should be 10", 10, baseAC);

    // Equipping a +0 RoundShield should add exactly +1 AC
    RoundShield shield = new RoundShield();
    hero.belongings.weapon = shield;
    hero.updateAC();
    assertEquals("RoundShield (+0) should add +1 AC", baseAC + 1, hero.AC);
}

@Test
public void testUpgradedShieldAddsEnhancementBonus() {
    Hero hero = new Hero();
    hero.DEX = 10;

    RoundShield shield = new RoundShield();
    // Simulate a +2 upgraded shield (level() returns upgrade level)
    shield.upgrade();
    shield.upgrade();
    hero.belongings.weapon = shield;
    hero.updateAC();

    // +1 base shield bonus + 2 upgrade levels = +3 AC total
    assertEquals("RoundShield (+2) should add +3 AC (1 base + 2 enhancement)", 13, hero.AC);
}

@Test
public void testShieldStacksWithArmor() {
    Hero hero = new Hero();
    hero.DEX = 13; // +1 mod

    // Equip leather armor (tier 2 → +2 AC bonus from ACBonus())
    hero.belongings.armor = new LeatherArmor();
    RoundShield shield = new RoundShield();
    hero.belongings.weapon = shield;
    hero.updateAC();

    // 10 + 1 (DEX) + 2 (leather) + 1 (shield) = 14
    assertEquals("Shield bonus stacks with armor and DEX", 14, hero.AC);
}

@Test
public void testNonShieldWeaponDoesNotAddAC() {
    Hero hero = new Hero();
    hero.DEX = 10;
    hero.belongings.weapon = new WornShortsword();
    hero.updateAC();
    assertEquals("A non-shield weapon must not add any AC", 10, hero.AC);
}

}
