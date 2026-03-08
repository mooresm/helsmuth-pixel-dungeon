/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Rat extends Mob {

	{
		spriteClass = RatSprite.class;

		defenseSkill = 2; // Keep for compatibility

		// D&D Dire Rat ability scores
		STR = 10;
		DEX = 17; // +3 modifier
		CON = 12;
		INT = 2;
		WIS = 12;
		CHA = 4;

		// D&D AC: 10 base + 3 DEX + 1 natural + 1 size = 15
		AC = 10 + statBonus(DEX) + 1 + 1;
		HP = HT = Random.IntRange(1, 8) + statBonus(CON);
		loot = MysteryMeat.class;
		lootChance = 0.3333f;

		maxLvl = 5;
	}
	@Override
	protected boolean act() {
		if (alignment != Alignment.ALLY
				&& Dungeon.level.heroFOV[pos]
				&& Dungeon.hero.armorAbility instanceof Ratmogrify){
			alignment = Alignment.NEUTRAL;
			if (enemy == Dungeon.hero) enemy = null;
			if (state == SLEEPING) state = WANDERING;
		}
		return super.act();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		// BAB 0 + DEX +3 + size +1 = +4
		return 0 + statBonus(DEX) + 1;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 1);
	}

	private static final String RAT_ALLY = "rat_ally";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (alignment == Alignment.ALLY) bundle.put(RAT_ALLY, true);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(RAT_ALLY)) alignment = Alignment.ALLY;
	}
}
