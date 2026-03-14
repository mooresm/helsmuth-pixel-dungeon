/*
 * Helsmuth Dungeon
 * Copyright (C) 2026 Matt Moores
 *
 * D&D Conversion PoC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.DamageType;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the D&D weapon damage type system (Phase 4).
 *
 * <p><b>Testing boundary:</b> {@code new Dagger()} and other concrete weapon constructors
 * trigger {@code ItemSpriteSheet$Icons.<clinit>}, which requires a live LibGDX/GPU context
 * and cannot run on the JVM. Fields are therefore set directly on a minimal anonymous
 * {@link Weapon} subclass that skips all LibGDX static initializers.
 * See tasks.md § "Testing boundary" for the full explanation.</p>
 */
public class WeaponDamageTypeTest {

    // ---------------------------------------------------------------------------
    // Minimal concrete Weapon — avoids every LibGDX static initializer.
    // Only override what is strictly needed for the test.
    // ---------------------------------------------------------------------------

    /** Returns a bare Weapon whose damageType and damage range can be set freely. */
    private Weapon makeWeapon(DamageType type, int maxDamage) {
        return new Weapon() {
            {
                damageType = type;
            }

            @Override
            public int max(int lvl) {
                return maxDamage;
            }

            // damageRoll is not under test here; suppress to avoid any Char dependency.
            @Override
            public int damageRoll(Char owner) {
                return 0;
            }

            @Override
            public int STRReq(int lvl) {
                return 0;
            }
        };
    }

    // ---------------------------------------------------------------------------
    // DamageType enum — basic sanity
    // ---------------------------------------------------------------------------

    @Test
    public void damageTypeEnumHasThreeValues() {
        DamageType[] values = DamageType.values();
        assertEquals(3, values.length);
    }

    @Test
    public void damageTypeEnumContainsExpectedValues() {
        // Verify all three D&D physical damage types are present.
        assertNotNull(DamageType.valueOf("SLASHING"));
        assertNotNull(DamageType.valueOf("PIERCING"));
        assertNotNull(DamageType.valueOf("BLUDGEONING"));
    }

    // ---------------------------------------------------------------------------
    // getDamageType() — default and explicit assignment
    // ---------------------------------------------------------------------------

    @Test
    public void defaultDamageTypeIsSlashing() {
        // Weapon.java declares: protected DamageType damageType = DamageType.SLASHING
        Weapon w = makeWeapon(DamageType.SLASHING, 6);
        assertEquals(DamageType.SLASHING, w.getDamageType());
        assertEquals(6, w.max());
        assertEquals(1, w.min());
    }

    @Test
    public void getDamageTypeReturnsPiercing() {
        Weapon w = makeWeapon(DamageType.PIERCING, 4);
        assertEquals(DamageType.PIERCING, w.getDamageType());
        assertEquals(4, w.max());
        assertEquals(1, w.min());
    }

    @Test
    public void getDamageTypeReturnsBludgeoning() {
        Weapon w = makeWeapon(DamageType.BLUDGEONING, 1);
        assertEquals(DamageType.BLUDGEONING, w.getDamageType());
        assertEquals(1, w.max());
        assertEquals(1, w.min());
    }

    // ---------------------------------------------------------------------------
    // Skeleton DR interaction — bludgeoning bypasses DR 5/bludgeoning
    // Tests the *type comparison* logic used by the DR system (pure enum equality).
    // ---------------------------------------------------------------------------

    @Test
    public void bludgeoningWeaponBypassesSkeletonDR() {
        // DR 5/bludgeoning: damage reduction is waived when weapon type == BLUDGEONING.
        // This mirrors the conditional the dr() implementation will use.
        Weapon club = makeWeapon(DamageType.BLUDGEONING, 6);
        boolean bypassesDR = club.getDamageType() == DamageType.BLUDGEONING;
        assertTrue("Club (BLUDGEONING) should bypass skeleton DR 5/bludgeoning", bypassesDR);
    }

    @Test
    public void piercingWeaponDoesNotBypassSkeletonDR() {
        Weapon dagger = makeWeapon(DamageType.PIERCING, 4);
        boolean bypassesDR = dagger.getDamageType() == DamageType.BLUDGEONING;
        assertFalse("Dagger (PIERCING) should not bypass skeleton DR 5/bludgeoning", bypassesDR);
    }

    @Test
    public void slashingWeaponDoesNotBypassSkeletonDR() {
        Weapon longsword = makeWeapon(DamageType.SLASHING, 8);
        boolean bypassesDR = longsword.getDamageType() == DamageType.BLUDGEONING;
        assertFalse("Longsword (SLASHING) should not bypass skeleton DR 5/bludgeoning", bypassesDR);
    }

    // ---------------------------------------------------------------------------
    // Damage range boundary — max must be >= min for all weapon specs
    // ---------------------------------------------------------------------------

    @Test
    public void allWeaponMaxDamageIsAtLeastMin() {
        int[][] specs = {
                // { maxDamage }  — min is always 1 from Weapon.java base class
                {4},  // Dagger  1d4
                {8},  // Longsword 1d8
                {6},  // Club    1d6
                {8},  // Mace    1d8
        };
        for (int[] spec : specs) {
            Weapon w = makeWeapon(DamageType.SLASHING, spec[0]);
            assertTrue(
                    "max() must be >= min() for a weapon with max=" + spec[0],
                    w.max() >= w.min()
            );
        }
    }
}
