package com.shatteredpixel.shatteredpixeldungeon.actors;

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
}
