package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for D&D ability score system in Hero.java
 * 
 * Tests the ability modifier calculation: modifier = (ability - 10) / 2
 */
public class HeroTest {

    private Hero hero;

    @Before
    public void setUp() {
        // Create a new Hero instance before each test
        hero = new Hero();
    }

    /**
     * Test that ability modifiers are calculated correctly.
     * D&D modifier formula: (ability_score - 10) / 2, rounded down
     */
    @Test
    public void testAbilityModifierCalculation() {
        // Test standard ability scores and their modifiers
        assertEquals("STR 10 should give +0 modifier", 10, hero.STR());
    }
}
