package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import org.junit.Test;

import static org.junit.Assert.*;

public class WraithDRTest {


// ==================== WRAITH: Incorporeal subtype ====================

    @Test
    public void testNullWeaponIsNotMagical() {
        assertFalse("No weapon (unarmed/non-Hero) is never magical",
                Wraith.isMagicalWeapon(null));
    }

    @Test
    public void testNonmagicalWeaponAlwaysDeflected() {
        // isDeflected(false, anything) must always be true
        assertTrue("Nonmagical weapon is always deflected (roll true)",
                Wraith.isDeflected(false, true));
        assertTrue("Nonmagical weapon is always deflected (roll false)",
                Wraith.isDeflected(false, false));
    }

    @Test
    public void testMagicalWeaponDeflectedOnTrueRoll() {
        assertTrue("Magical weapon is deflected when deflectRoll is true",
                Wraith.isDeflected(true, true));
    }

    @Test
    public void testMagicalWeaponLandsOnFalseRoll() {
        assertFalse("Magical weapon lands when deflectRoll is false",
                Wraith.isDeflected(true, false));
    }

    @Test
    public void testWraithStats() {
        Wraith wraith = new Wraith();
        assertEquals("Wraith AC should be 15", 15, wraith.AC);  // 10 + DEX(+3) + natural(2)
        assertTrue("Wraith HP >= 5",  wraith.HP >= 5);
        assertTrue("Wraith HP <= 60", wraith.HP <= 60);
        assertEquals("Wraith attack should be +5", 5, wraith.attackSkill(wraith));
        assertTrue("Wraith damage >= 1", wraith.damageRoll() >= 1);
        assertTrue("Wraith damage <= 4", wraith.damageRoll() <= 4);
    }
}
