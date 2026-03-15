/*
 * Helsmuth Dungeon
 * Copyright (C) 2026 Matt Moores
 *
 * D&D Conversion PoC — Phase 6: Skeleton DR 5/bludgeoning
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.DamageType;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for Skeleton.drForWeaponType() — the pure DR 5/bludgeoning logic.
 *
 * <p><b>Testing boundary:</b> {@code new Skeleton()} triggers {@code SkeletonSprite.<clinit>}
 * which requires a live LibGDX/GPU context.  The DR logic is therefore extracted into a
 * package-private static helper ({@link Skeleton#drForWeaponType}) that can be called
 * without instantiating Skeleton.  See tasks.md § "Testing boundary".</p>
 *
 * <p>D&D 3.5 rule modelled here:
 * <ul>
 *   <li>DR 5/bludgeoning: subtract 5 from every hit that is NOT bludgeoning-typed.</li>
 *   <li>A bludgeoning weapon (club, mace, heavy mace, …) bypasses the reduction entirely.</li>
 *   <li>Natural attacks (null weapon type) are treated as untyped and are subject to DR.</li>
 * </ul>
 * </p>
 */
public class SkeletonDRTest {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    @Test
    public void drAmountIs5() {
        assertEquals("D&D 3.5 Human Skeleton has DR 5", 5, Skeleton.DR_AMOUNT);
    }

    @Test
    public void drBypassTypeIsBludgeoning() {
        assertEquals("Skeleton DR is bypassed by bludgeoning", DamageType.BLUDGEONING, Skeleton.DR_BYPASS_TYPE);
    }

    // -------------------------------------------------------------------------
    // drForWeaponType — bypass cases
    // -------------------------------------------------------------------------

    @Test
    public void bludgeoningReturnsDRZero() {
        assertEquals("Bludgeoning weapon bypasses DR 5 entirely",
                0, Skeleton.drForWeaponType(DamageType.BLUDGEONING));
    }

    // -------------------------------------------------------------------------
    // drForWeaponType — non-bypass cases
    // -------------------------------------------------------------------------

    @Test
    public void piercingReturnsDRAmount() {
        assertEquals("Piercing weapon is subject to DR 5",
                Skeleton.DR_AMOUNT, Skeleton.drForWeaponType(DamageType.PIERCING));
    }

    @Test
    public void slashingReturnsDRAmount() {
        assertEquals("Slashing weapon is subject to DR 5",
                Skeleton.DR_AMOUNT, Skeleton.drForWeaponType(DamageType.SLASHING));
    }

    @Test
    public void nullWeaponTypeReturnsDRAmount() {
        // null represents a natural attack (no weapon). Natural attacks are subject to DR.
        assertEquals("Natural attack (no weapon type) is subject to DR 5",
                Skeleton.DR_AMOUNT, Skeleton.drForWeaponType(null));
    }

    // -------------------------------------------------------------------------
    // Exhaustive enum coverage — all DamageType values accounted for
    // -------------------------------------------------------------------------

    @Test
    public void exactlyOneTypeBypassesDR() {
        int bypassCount = 0;
        for (DamageType dt : DamageType.values()) {
            if (Skeleton.drForWeaponType(dt) == 0) bypassCount++;
        }
        assertEquals("Exactly one damage type bypasses Skeleton DR", 1, bypassCount);
    }

    @Test
    public void allNonBludgeoningTypesAreSubjectToDR() {
        for (DamageType dt : DamageType.values()) {
            if (dt != DamageType.BLUDGEONING) {
                assertEquals("Non-bludgeoning type " + dt + " must be subject to DR 5",
                        Skeleton.DR_AMOUNT, Skeleton.drForWeaponType(dt));
            }
        }
    }
}
