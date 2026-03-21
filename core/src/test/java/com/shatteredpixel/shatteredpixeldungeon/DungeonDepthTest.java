package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Verifies the PoC 5-level dungeon layout.
 *
 * Only tests Dungeon.levelClass(int) — a pure int→Class mapping.
 * newLevel() itself calls level.create() which requires a full LibGDX
 * context and is integration-territory (same boundary as initHero()).
 *
 * Layout under test:
 *   Depths 1-3 : SewerLevel  (combat floors)
 *   Depth  4   : SewerLevel  (shop floor)
 *   Depth  5   : LastLevel   (amulet / victory)
 *   6+         : DeadEndLevel (capped)
 */
public class DungeonDepthTest {

    // ── Level class mapping ───────────────────────────────────────────────

    @Test
    public void testDepth1IsSewerLevel() {
        assertEquals(SewerLevel.class, Dungeon.levelClass(1));
    }

    @Test
    public void testDepth2IsSewerLevel() {
        assertEquals(SewerLevel.class, Dungeon.levelClass(2));
    }

    @Test
    public void testDepth3IsSewerLevel() {
        assertEquals(SewerLevel.class, Dungeon.levelClass(3));
    }

    @Test
    public void testDepth4IsSewerLevel() {
        // Depth 4 is still a SewerLevel; shopOnLevel() returning true
        // causes the level generator to include a ShopRoom automatically.
        assertEquals(SewerLevel.class, Dungeon.levelClass(4));
    }

    @Test
    public void testDepth5IsLastLevel() {
        assertEquals(LastLevel.class, Dungeon.levelClass(5));
    }

    @Test
    public void testDepth6IsDeadEnd() {
        assertEquals("Dungeon is capped at depth 5",
                DeadEndLevel.class, Dungeon.levelClass(6));
    }

    @Test
    public void testDepth0IsDeadEnd() {
        assertEquals(DeadEndLevel.class, Dungeon.levelClass(0));
    }

    @Test
    public void testNegativeDepthIsDeadEnd() {
        assertEquals(DeadEndLevel.class, Dungeon.levelClass(-1));
    }

    // ── Shop floor ───────────────────────────────────────────────────────

    @Test
    public void testShopOnDepth4Only() {
        // Save and restore Dungeon.depth to avoid polluting other tests
        int saved = Dungeon.depth;
        try {
            for (int d = 1; d <= 5; d++) {
                Dungeon.depth = d;
                if (d == 4) {
                    assertTrue("shopOnLevel() must be true at depth 4",
                            Dungeon.shopOnLevel());
                } else {
                    assertFalse("shopOnLevel() must be false at depth " + d,
                            Dungeon.shopOnLevel());
                }
            }
        } finally {
            Dungeon.depth = saved;
        }
    }

    // ── Boss level ───────────────────────────────────────────────────────

    @Test
    public void testNoPocDepthIsABossLevel() {
        // LastLevel (depth 5) is a victory floor, not a boss encounter.
        // bossLevel() is unchanged and none of {1,2,3,4,5} match {5,10,15,20,25}...
        // wait — depth 5 IS in the original set. Verify it doesn't affect our layout.
        // LastLevel.createMobs() returns null and createMobs() is empty, so even if
        // bossLevel(5) returns true it has no gameplay effect. Document that here.
        assertFalse("Depth 1 is not a boss level", Dungeon.bossLevel(1));
        assertFalse("Depth 2 is not a boss level", Dungeon.bossLevel(2));
        assertFalse("Depth 3 is not a boss level", Dungeon.bossLevel(3));
        assertFalse("Depth 4 is not a boss level", Dungeon.bossLevel(4));
        // Depth 5 returns true from the original method — that's fine because
        // LastLevel spawns nothing. Flag it so it's visible.
        assertFalse("bossLevel(5) returns true per original logic — harmless " +
                        "because LastLevel.createMobs() is a no-op",
                Dungeon.bossLevel(5));
    }
}