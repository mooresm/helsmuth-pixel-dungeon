package com.shatteredpixel.shatteredpixeldungeon.actors;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for D&D ability score system
 * 
 * Tests the ability modifier calculation and stat system integration
 */
public class CharTest {

    // ==================== STAT BONUS CALCULATION TESTS ====================
    
    /**
     * Test that ability modifiers are calculated correctly.
     * D&D modifier formula: (ability_score - 10) / 2 for scores >= 10
     *                      (ability_score - 11) / 2 for scores < 10
     * This ensures proper rounding down for both positive and negative modifiers
     */
    @Test
    public void testStatModifierCalculation() {
        // Test standard ability scores and their modifiers
        assertEquals("STR 10 should give +0 modifier", 0, Char.statBonus(10));
        assertEquals("STR 11 should give +0 modifier", 0, Char.statBonus(11));
        assertEquals("STR 12 should give +1 modifier", 1, Char.statBonus(12));
        assertEquals("STR 13 should give +1 modifier", 1, Char.statBonus(13));
        assertEquals("STR 14 should give +2 modifier", 2, Char.statBonus(14));
        assertEquals("STR 15 should give +2 modifier", 2, Char.statBonus(15));
        assertEquals("STR 16 should give +3 modifier", 3, Char.statBonus(16));
        assertEquals("STR 18 should give +4 modifier", 4, Char.statBonus(18));
        assertEquals("STR 20 should give +5 modifier", 5, Char.statBonus(20));
        assertEquals("STR 22 should give +6 modifier", 6, Char.statBonus(22));
    }

    @Test
    public void testStatModifierCalculation_NegativeModifiers() {
        // Test below-average ability scores
        assertEquals("STR 9 should give -1 modifier", -1, Char.statBonus(9));
        assertEquals("STR 8 should give -1 modifier", -1, Char.statBonus(8));
        assertEquals("STR 7 should give -2 modifier", -2, Char.statBonus(7));
        assertEquals("STR 6 should give -2 modifier", -2, Char.statBonus(6));
        assertEquals("STR 5 should give -3 modifier", -3, Char.statBonus(5));
        assertEquals("STR 4 should give -3 modifier", -3, Char.statBonus(4));
        assertEquals("STR 3 should give -4 modifier", -4, Char.statBonus(3));
        assertEquals("STR 2 should give -4 modifier", -4, Char.statBonus(2));
        assertEquals("STR 1 should give -5 modifier", -5, Char.statBonus(1));
    }

    @Test
    public void testStatModifierCalculation_EdgeCases() {
        // Test boundary conditions
        assertEquals("STR 0 should give -5 modifier", -5, Char.statBonus(0));
        assertEquals("STR 30 should give +10 modifier", 10, Char.statBonus(30));
        
        // Test the critical boundary at 10-11 where formula changes
        assertEquals("Boundary check: 9 -> -1", -1, Char.statBonus(9));
        assertEquals("Boundary check: 10 -> 0", 0, Char.statBonus(10));
        assertEquals("Boundary check: 11 -> 0", 0, Char.statBonus(11));
        assertEquals("Boundary check: 12 -> +1", 1, Char.statBonus(12));
    }

    // ==================== FORMULA SYMMETRY TESTS ====================
    
    @Test
    public void testStatBonusSymmetry() {
        // Test that positive and negative modifiers are symmetric
        // For every +X at (10+2X), there should be -X at (10-2X-1)
        assertEquals("+1 at 12", 1, Char.statBonus(12));
        assertEquals("-1 at 8", -1, Char.statBonus(8));
        
        assertEquals("+2 at 14", 2, Char.statBonus(14));
        assertEquals("-2 at 6", -2, Char.statBonus(6));
        
        assertEquals("+3 at 16", 3, Char.statBonus(16));
        assertEquals("-3 at 4", -3, Char.statBonus(4));
        
        assertEquals("+4 at 18", 4, Char.statBonus(18));
        assertEquals("-4 at 2", -4, Char.statBonus(2));
    }


    // ==================== REGRESSION TESTS ====================
    
    @Test
    public void testStatBonusNeverReturnsNaN() {
        // Ensure the formula always returns valid integers
        for (int stat = 0; stat <= 30; stat++) {
            int bonus = Char.statBonus(stat);
            assertFalse("Bonus should never be NaN", Double.isNaN(bonus));
            assertTrue("Bonus should be finite", Double.isFinite(bonus));
        }
    }

    @Test
    public void testStatBonusMonotonicallyIncreasing() {
        // Verify that higher stats always give equal or better bonuses
        for (int stat = 1; stat <= 29; stat++) {
            int currentBonus = Char.statBonus(stat);
            int nextBonus = Char.statBonus(stat + 1);
            assertTrue("Stat " + (stat + 1) + " bonus should be >= stat " + stat + " bonus",
                      nextBonus >= currentBonus);
        }
    }

    @Test
    public void testStatBonusIncrementsCorrectly() {
        // Verify that bonuses increment by 1 every 2 points
        for (int stat = 10; stat <= 28; stat += 2) {
            int bonus1 = Char.statBonus(stat);
            int bonus2 = Char.statBonus(stat + 1);
            int bonus3 = Char.statBonus(stat + 2);
            
            assertEquals("Both " + stat + " and " + (stat + 1) + " should give same bonus",
                        bonus1, bonus2);
            assertEquals("Stat " + (stat + 2) + " should be +1 higher than " + stat,
                        bonus1 + 1, bonus3);
        }
    }
}
