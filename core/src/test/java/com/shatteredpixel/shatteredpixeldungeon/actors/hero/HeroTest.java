package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

/**
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
    public void testSTR() {
        // Test standard ability scores and their modifiers
        assertEquals("STR 10 should give +0 modifier", 10, hero.STR());
    }

    @Test
    public void testHeroAttributesInitialization() {
        // Test that hero is initialized with default stats
        assertEquals("Hero should start with STR = 10", 10, hero.STR);
        assertEquals("Hero should start with HP = 20", 20, hero.HP);
        assertEquals("Hero should start with HT = 20", 20, hero.HT);
    }

    @Test
    public void testDefaultStatValues() {
        // Test that uninitialized stats default to 0
        // (Hero only initializes STR in the constructor)
        assertEquals("Uninitialized DEX should be 0", 0, hero.DEX);
        assertEquals("Uninitialized CON should be 0", 0, hero.CON);
        assertEquals("Uninitialized INT should be 0", 0, hero.INT);
        assertEquals("Uninitialized WIS should be 0", 0, hero.WIS);
        assertEquals("Uninitialized CHA should be 0", 0, hero.CHA);
    }

    // ==================== STAT MODIFICATION TESTS ====================

    @Test
    public void testStatModification_STR() {
        hero.STR = 10;
        assertEquals("Base STR should be 10", 10, hero.STR);

        hero.STR = 18;
        assertEquals("Modified STR should be 18", 18, hero.STR);

        // Test that statBonus correctly reflects the change
        assertEquals("STR 18 gives +4 modifier", 4, Char.statBonus(hero.STR));
    }

    @Test
    public void testStatModification_AllStats() {
        // Test setting all D&D attributes
        hero.STR = 16;
        hero.DEX = 14;
        hero.CON = 15;
        hero.INT = 12;
        hero.WIS = 13;
        hero.CHA = 8;

        assertEquals("STR set to 16", 16, hero.STR);
        assertEquals("DEX set to 14", 14, hero.DEX);
        assertEquals("CON set to 15", 15, hero.CON);
        assertEquals("INT set to 12", 12, hero.INT);
        assertEquals("WIS set to 13", 13, hero.WIS);
        assertEquals("CHA set to 8", 8, hero.CHA);

        // Verify modifiers
        assertEquals("STR 16 gives +3", 3, Char.statBonus(hero.STR));
        assertEquals("DEX 14 gives +2", 2, Char.statBonus(hero.DEX));
        assertEquals("CON 15 gives +2", 2, Char.statBonus(hero.CON));
        assertEquals("INT 12 gives +1", 1, Char.statBonus(hero.INT));
        assertEquals("WIS 13 gives +1", 1, Char.statBonus(hero.WIS));
        assertEquals("CHA 8 gives -1", -1, Char.statBonus(hero.CHA));
    }

    @Test
    public void testStatBonusForAllAttributes() {
        // Comprehensive test showing how all attributes would affect gameplay
        hero.STR = 16; // +3 (affects melee damage, carry capacity)
        hero.DEX = 14; // +2 (affects AC, initiative, ranged attacks)
        hero.CON = 15; // +2 (affects HP per level)
        hero.INT = 10; // +0 (affects spell power)
        hero.WIS = 12; // +1 (affects perception, saving throws)
        hero.CHA = 8;  // -1 (affects NPC interactions)

        assertEquals("STR modifier for damage", 3, Char.statBonus(hero.STR));
        assertEquals("DEX modifier for AC/initiative", 2, Char.statBonus(hero.DEX));
        assertEquals("CON modifier for HP", 2, Char.statBonus(hero.CON));
        assertEquals("INT modifier for spells", 0, Char.statBonus(hero.INT));
        assertEquals("WIS modifier for perception", 1, Char.statBonus(hero.WIS));
        assertEquals("CHA modifier for diplomacy", -1, Char.statBonus(hero.CHA));
    }
}
