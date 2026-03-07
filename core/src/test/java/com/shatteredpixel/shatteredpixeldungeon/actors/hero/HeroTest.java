package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.utils.Random;

/**
 * Tests for Hero ability score fields and D&D modifier calculations.
 *
 * These tests operate directly on Hero fields and Char.statBonus() — no
 * LibGDX context or game engine required.
 */
public class HeroTest {

    private Hero hero;

    @Before
    public void setUp() {
        hero = new Hero();
        // Push a random seed to the RNG for reproducible tests
        Random.pushGenerator(12345L);
    }

    @After
    public void tearDown() {
        Random.popGenerator();
    }

    @Test
    public void testHeroAttributesInitialization() {
        assertEquals("Hero should start with STR = 10", 10, hero.STR);
        assertEquals("Hero should start with HP = 20", 20, hero.HP);
        assertEquals("Hero should start with HT = 20", 20, hero.HT);
    }

    @Test
    public void testDefaultStatValues() {
        assertEquals("Uninitialized DEX should be 0", 0, hero.DEX);
        assertEquals("Uninitialized CON should be 0", 0, hero.CON);
        assertEquals("Uninitialized INT should be 0", 0, hero.INT);
        assertEquals("Uninitialized WIS should be 0", 0, hero.WIS);
        assertEquals("Uninitialized CHA should be 0", 0, hero.CHA);
    }

    @Test
    public void testStatModification_STR() {
        hero.STR = 10;
        assertEquals(10, hero.STR);
        hero.STR = 18;
        assertEquals(18, hero.STR);
        assertEquals("STR 18 gives +4 modifier", 4, Char.statBonus(hero.STR));
    }

    @Test
    public void testStatModification_AllStats() {
        hero.STR = 16; hero.DEX = 14; hero.CON = 15;
        hero.INT = 12; hero.WIS = 13; hero.CHA = 8;

        assertEquals(3,  Char.statBonus(hero.STR));
        assertEquals(2,  Char.statBonus(hero.DEX));
        assertEquals(2,  Char.statBonus(hero.CON));
        assertEquals(1,  Char.statBonus(hero.INT));
        assertEquals(1,  Char.statBonus(hero.WIS));
        assertEquals(-1, Char.statBonus(hero.CHA));
    }

    // ========== D&D HP System Tests ==========

    /**
     * Test that WARRIOR gains HP using d10 hit die when leveling up.
     * HP gain should be: 1d10 + CON modifier (between 3 and 12 for CON 14)
     */
    @Test
    public void testWarriorLevelUpHPGain() {
        hero.heroClass = HeroClass.WARRIOR;
        hero.CON = 14; // +2 modifier
        hero.lvl = 1;
        hero.HT = 12; // Starting HP for Fighter
        hero.HP = 12;
        hero.HTBoost = 10; // Should accumulate dice rolls here

        // Simulate level up to level 2
        int htBefore = hero.HT;
        hero.lvl++;
        hero.HTBoost += Random.IntRange(1, 10);

        // When updateHT is called with boostHP=true during level up,
        // HTBoost should increase by (1d10 + conMod)
        // Then HT should be recalculated as: (10 + conMod()) + HTBoost
        hero.updateHT( true );
        int gain = hero.HT - htBefore;

        // The HP gain should be between min (1 + 2) and max (10 + 2)
        int minGain = 1 + Char.statBonus(hero.CON); // 3
        int maxGain = 10 + Char.statBonus(hero.CON); // 12

        // After the implementation, we'll verify the gain is in range
        // For now, we document the expected behavior
        assertTrue("Min HP gain for WARRIOR should be 1d10 + conMod (min 3)", gain >= minGain);
        assertTrue("Max HP gain for WARRIOR should be 1d10 + conMod (max 12)", gain <= maxGain);
    }

    /**
     * Test that non-WARRIOR classes gain +5 HP per level (original behavior)
     */
    @Test
    public void testNonWarriorLevelUpHPGain() {
        hero.heroClass = HeroClass.MAGE;
        hero.lvl = 1;
        hero.HT = 20;
        hero.HTBoost = 0;

        // After level up, should gain +5 HP (original formula: 20 + 5*(lvl-1))
        hero.lvl = 2;
        hero.updateHT(true);

        int expectedHT = 20 + 5 * (hero.lvl - 1); // 20 + 5 = 25
        assertEquals("Non-WARRIOR should gain +5 HP per level", expectedHT, hero.HT);
    }

    /**
     * Test that updateHT() can be called multiple times without re-rolling HP.
     * This is critical because updateHT is called when equipment changes, not just level up.
     */
    @Test
    public void testUpdateHTDoesNotRerollHP() {
        hero.heroClass = HeroClass.WARRIOR;
        hero.CON = 14;
        hero.lvl = 3;
        hero.HTBoost = 17; // Simulated cumulative HP from level-ups

        // First calculation
        hero.updateHT(false);
        int firstHT = hero.HT;

        // Second calculation (e.g., after equipping a ring)
        hero.updateHT(false);
        int secondHT = hero.HT;

        assertEquals("updateHT should give consistent results without re-rolling",
                     firstHT, secondHT);
    }

    /**
     * Test that HTBoost is properly used to store cumulative HP gains
     * and persists across recalculations.
     */
    @Test
    public void testHTBoostPersistsInFormula() {
        hero.heroClass = HeroClass.WARRIOR;
        hero.CON = 14; // +2 modifier
        hero.lvl = 1;
        hero.HTBoost = 10;

        // Initial state: HT should be 10 + conMod() + HTBoost = 12 + 0 = 12
        hero.updateHT(false);
        assertEquals("Initial WARRIOR HT should be 10 + conMod()",
                     10 + Char.statBonus(hero.CON), hero.HT);

        // Simulate gaining HP from level-up (e.g., rolled a 7, +2 con = 9 HP)
        hero.HTBoost += 9;
        hero.lvl = 2;

        // Now HT should include the HTBoost
        hero.updateHT(true);
        int expectedHT = hero.lvl *  Char.statBonus(hero.CON) + hero.HTBoost; // 12 + 9 = 21
        assertEquals("HT should include HTBoost from level-up rolls", expectedHT, hero.HT);
    }

    /**
     * Test that HTBoost starts at 0 for new heroes
     */
    @Test
    public void testHTBoostInitialization() {
        assertEquals("HTBoost should start at 0", 0, hero.HTBoost);
    }
}
