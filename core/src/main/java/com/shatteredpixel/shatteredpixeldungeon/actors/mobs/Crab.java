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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrabSprite;
import com.watabou.utils.Random;

public class Crab extends Mob {

	{
		spriteClass = CrabSprite.class;
		
		defenseSkill = 5; // Keep for compatibility
		baseSpeed = 2f;
		EXP = 4;
		maxLvl = 9;

		// D&D medium monstrous crab ability scores
		STR = 15;
		DEX = 11;
		CON = 12;
		WIS = 11;
		CHA = 2;

		HP = HT = Random.IntRange(1, 8) + Random.IntRange(1, 8) + Random.IntRange(1, 8) + 6;
		AC = 10 + statBonus(DEX) + 6;

		loot = MysteryMeat.class;
		lootChance = 0.167f;
	}
	
	@Override
	public int damageRoll() {
		return Random.IntRange( 3, 8 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 4;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 4);
	}
}
