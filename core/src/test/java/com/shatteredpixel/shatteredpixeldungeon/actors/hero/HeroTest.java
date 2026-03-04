package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

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
}
