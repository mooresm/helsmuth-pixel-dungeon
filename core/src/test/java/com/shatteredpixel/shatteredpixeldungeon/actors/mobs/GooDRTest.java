package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import static org.junit.Assert.assertEquals;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

import org.junit.Test;

public class GooDRTest {
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    @Test
    public void drAmountIs5() {
        assertEquals("D&D 3.5 Black Pudding has DR 10", 10, Goo.SPLIT_DR);
    }

    @Test
    public void drBypassTypeIsBludgeoning() {
        assertEquals("Black Pudding DR is bypassed by bludgeoning", Weapon.DamageType.BLUDGEONING, Goo.SPLIT_BYPASS);
    }

    @Test
    public void testGooSplitMechanic_bludgeoningBypasses() {
        assertEquals("Bludgeoning bypasses split — DR should be 0",
                0, Goo.drForDamageType(Weapon.DamageType.BLUDGEONING));
    }

    @Test
    public void testGooSplitMechanic_slashingNegated() {
        assertEquals("Slashing triggers split — DR should equal SPLIT_DR sentinel",
                Goo.SPLIT_DR, Goo.drForDamageType(Weapon.DamageType.SLASHING));
    }

    @Test
    public void testGooSplitMechanic_piercingNegated() {
        assertEquals("Piercing triggers split — DR should equal SPLIT_DR sentinel",
                Goo.SPLIT_DR, Goo.drForDamageType(Weapon.DamageType.PIERCING));
    }

    @Test
    public void testGooSplitMechanic_naturalAttackNegated() {
        // null weapon type = natural attack (no weapon) — also triggers split
        assertEquals("Natural attack (null type) triggers split",
                Goo.SPLIT_DR, Goo.drForDamageType(null));
    }
}
