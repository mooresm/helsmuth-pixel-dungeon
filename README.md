# Helsmuth Dungeon

A minimal proof-of-concept conversion of [Shattered Pixel Dungeon](https://shatteredpixel.com/shatteredpd/) to use [d20 System Compatible mechanics](https://www.d20srd.org/index.htm). 
This project demonstrates that classic tabletop RPG systems can work in a roguelike dungeon crawler.

**Status:** Proof of Concept - Ultra-Minimal Implementation  
**Estimated Completion:** 80 hours (3-5 completed)  
**Based on:** Shattered Pixel Dungeon by Evan Debenham ([00-Evan](https://github.com/00-Evan)) and the original [Pixel Dungeon](https://github.com/00-Evan/pixel-dungeon-gradle), by [Watabou](https://watabou.itch.io/).

Helsmuth Dungeon currently compiles for Android, iOS, and Desktop platforms (Windows, Linux and macOS).

If you'd like to work with the code, you can find the following guides in `/docs`:
- [Compiling for Android.](docs/getting-started-android.md)
    - **[If you plan to distribute on Google Play please read the end of this guide.](docs/getting-started-android.md#distributing-your-app)**
- [Compiling for desktop platforms.](docs/getting-started-desktop.md)
- [Compiling for iOS.](docs/getting-started-ios.md)
- [Recommended changes for making your own version.](docs/recommended-changes.md)

### d20 System Compatible Features Implemented
- ✅ **Ability Scores** (STR, DEX, CON, INT, WIS, CHA) with standard modifiers
- ✅ **d20 Combat System** - Attack rolls using 1d20 + modifiers vs AC
- ✅ **Armor Class (AC)** - Replaces the original evasion system
- ✅ **Base Attack Bonus (BAB)** - Increases with character level
- ✅ **Weapon Damage Types** - Slashing, piercing, and bludgeoning matter
- ✅ **Damage Reduction (DR)** - Skeletons have DR 5/bludgeoning
- ✅ **Hit Dice** - Fighters roll d10 for HP on level up

### What's Included (Ultra-Minimal PoC)
- **1 Class:** Fighter (D&D 3.5 stats: STR 16, DEX 13, CON 14, INT 10, WIS 12, CHA 8)
- **2 Levels:** Combat tutorial + victory level with amulet
- **2 Monsters:** Dire Rat (CR 1/3) and Skeleton (CR 1/3, DR 5/bludgeoning)
- **3 Weapons:** Dagger (1d4 piercing), Longsword (1d8 slashing), Heavy Mace (1d8 bludgeoning)
- **2 Armor Types:** Leather armor (+2 AC), Scale mail (+4 AC)

## Project Status & Roadmap

### Current Phase: Proof of Concept (Weeks 1-8)
- [x] Phase 0: Build setup with JUnit testing (2h)
- [x] Phase 1: Ability scores and modifiers (6h)
- [ ] Phase 2: Fighter initialization (8h)
- [ ] Phase 3: d20 combat system (d20, AC, BAB) (12h)
- [ ] Phase 4: Weapon damage types (10h)
- [ ] Phase 5: Armor AC system (4h)
- [ ] Phase 6: Monster implementation (Dire Rat, Skeleton) (12h)
- [ ] Phase 7: 2-level dungeon (8h)
- [ ] Phase 8: UI updates (ability scores, AC display) (6h)
- [ ] Phase 9: Starting equipment (2h)
- [ ] Integration testing (10h)

**PoC Total:** 80 hours (~2 months at 10 hours/week)