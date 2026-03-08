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
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.watabou.utils.Random;

public class Gnoll extends Mob {
	
	{
		spriteClass = GnollSprite.class;

		defenseSkill = 4; // Keep for compatibility
		EXP = 2;
		maxLvl = 8;

		// D&D kobold ability scores
		STR = 9;
		DEX = 13;
		CON = 10;
		INT = 10;
		WIS = 9;
		CHA = 8;

		HP = HT = Random.IntRange(1, 8);
		AC = 10 + statBonus(DEX) + 1 + 1 + 2;

		loot = Gold.class;
		lootChance = 0.5f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 0, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		// BAB 1 + STR -1 + size +1 = +1
		return 1 + statBonus(STR) + 1;
	}
}
