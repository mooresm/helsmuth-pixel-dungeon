# The Full History of Shattered Pixel Dungeon

A summary of the project's evolution based on the commit history of [helsmuth-pixel-dungeon](https://github.com/mooresm/helsmuth-pixel-dungeon), covering **8,713 commits** from July 2014 to February 2026.

---

## Origins: Watabou's Pixel Dungeon (July 2014)

The story starts on **July 26, 2014**, when Watabou uploaded the source code of his Android roguelike *Pixel Dungeon* to GitHub. His 10 commits are purely foundational — the original game files, `.gitignore`, and minor level generation tweaks. Within days, Evan Debenham had already forked it and started making it his own.

---

## The Early Shattered Era: v0.1–v0.2 (August–December 2014)

Evan's first release, **v0.1.0** (August 2014), was essentially a balance pass and polish on the original. He quickly iterated into **v0.1.1**, introducing *Blandfruit* (a transformable food item), overhauling the Scroll of Lullaby, and tweaking the Ankh mechanics.

The big leap came with **v0.2.0** (September 2014), which introduced the game's first truly original system: **Artifacts**. Seven new equippable items replaced most of the old rings — the Cloak of Shadows (Rogue-exclusive), Horn of Plenty, Chalice of Blood, Cape of Thorns, Master Thieves' Armband, Talisman of Foresight, and Sandals of Nature. Rings were streamlined down to a meaningful core set, with six brand new ones added. This was the moment Shattered became a genuinely different game from the original.

**v0.2.1** (October 2014) overhauled the Ghost Quest with new boss monsters — the Fetid Rat and Gnoll Trickster — replacing the old rat skull fetch quest. **v0.2.2** added new plant types, new seeds, reworked the hero remains (bones) system, and polished gas mechanics. **v0.2.3** kept iterating: more artifacts, the Timekeeper's Hourglass, the Unstable Spellbook, a festive Rat King sprite for Christmas, and the first anti-farming measures to stop health potion grinding.

---

## Building the Foundation: v0.3–v0.4 (2015–2016)

**v0.3** (2015) was a major wand overhaul — new wand sprites and completely reworked mechanics. Evan also added a double-tap to search mechanic and a tutorial hint for it, showing early attention to new player experience.

**v0.4** (2016) introduced the **desktop/PC platform** — Shattered was no longer Android-only. Along with it came keyboard support, a no-lighting visual shader, proper text input windows for all languages, and early controller groundwork. v0.4 also expanded secret rooms significantly.

Throughout this period Evan maintained a relentless bugfix cadence — often multiple commits per day — while also adding new language translations (Hungarian, Esperanto, Indonesian, and others). The international community was clearly engaged early.

---

## The Hero Expansion Era: v0.5–v0.7 (2017–2019)

**v0.5.0** (early 2017) brought significant reworks to tiled visuals — custom wall layers, mass grave bone visuals, and multi-layer terrain. The Unstable Spellbook was reworked to grant empowered scroll effects.

**v0.6.0** (2017) was a landmark update introducing new room types including CaveRoom, and **v0.6.2** reworked the Freerunner subclass and added five new secret rooms. **v0.6.3–v0.6.4** added new weapons — including ranged options — and **v0.6.5** was a full glyph overhaul, reworking how armor enchantments worked. The Warlock and Huntress received notable buffs.

**v0.7.0** (2018) added 10 new spells, 8 new bomb types, and a new "not identified but not cursed" item state — a quality-of-life improvement that gave players more useful information. Hero unlock requirements and achievement badges were also added.

**v0.7.1** added furrowed grass mechanics for the Huntress. **v0.7.5** (late 2019) completely overhauled the **Tengu boss fight**, giving Tengu actual abilities and a more dynamic encounter. This was a major moment — Tengu had been a fairly flat boss until this point.

---

## Yog, Talents, and the New Bosses: v0.8–v0.9 (2020–2021)

**v0.8.0** (2020) was another landmark: a complete overhaul of the **Yog-Dzewa final boss fight**, which had previously been quite simple. Monks and Warlocks were also reworked, and desktop file handling was substantially improved.

**v0.9.0** (2020) introduced the **Talent system** — a grid of passive upgrades that gave each hero class meaningful build differentiation. This was arguably the most transformative single addition in the game's history, as it gave players a meaningful meta-progression layer mid-run. Evan spent months implementing talents tier by tier for every class and subclass. By v0.9.3–v0.9.4 (2021), the talent system was complete, the guidebook was overhauled, and hero info windows were rebuilt from scratch.

---

## The Mature Game: v1.0–v1.4 (2021–2022)

The v1.x era focused on polish and platform expansion. **v1.2.0** (2022) added full keyboard and keybind support with emulated mouse clicks, enabling proper desktop play. **v1.3.0** brought a new landscape hero select UI. **v1.4.0** overhauled the hero select screen for landscape orientation, reworked controller/keybind danger cycling, and added dev commentary entries to the in-game changelog dating back to v0.1.

---

## The Duelist and Cleric: v2.0–v3.x (2022–Present)

**v2.0.0** (released November 2022, developed throughout 2022) was one of the biggest updates ever: it introduced the **Duelist**, a brand new fifth hero class. The Duelist came with unique weapon abilities (every melee weapon had a special charged move), a full talent tree, starting thrown weapons, new subclasses, and dedicated splash art. This was over a year of work.

**v2.1–v2.4** (2023–2024) continued adding content: fungal sentries, crystal guardians, a wandmaker quest overhaul, the Blacksmith landmark, ring balance passes, and ongoing quality-of-life improvements. The catalog UI was completely rebuilt with a grid layout in **v2.5.0** (2024).

**v3.0.0** (developed 2024, released early 2025) introduced the **Cleric**, the sixth and most complex hero yet — a spellcaster with an entirely new resource system based on a Holy Tome, two subclasses (Paladin and Priest), extensive spell trees, and a unique ascension victory sprite. This was clearly the most complex hero addition to date, requiring months of iterative balancing.

**v3.1.0** (mid-2025) reworked the Warrior's shielding to be more active, and thrown weapons were rebuilt from scratch in **v3.2.0** — now working as upgradeable "sets" rather than disposable single-use items, a major mechanical shift for the Huntress in particular.

**v3.3.0** (late 2025 / early 2026) introduced a new imp vault quest, a skeleton key mechanic, a randomized run mode with dedicated badge, and various boss tweaks. The most recent release is **v3.3.5** (February 4, 2026).

---

## Contributor Summary

| Author | Commits | Role |
|---|---|---|
| **Evan Debenham** | 8,691 | Primary developer of Shattered Pixel Dungeon |
| **mooresm** | 12 | Fork owner — D&D OGL 3.5 conversion project |
| **Watabou** | 10 | Original Pixel Dungeon creator (foundational commits) |

---

## Key Milestones at a Glance

| Version | Year | Highlight |
|---|---|---|
| v0.1.0 | 2014 | Initial fork; balance and polish |
| v0.2.0 | 2014 | Artifacts system introduced |
| v0.2.1 | 2014 | Ghost Quest overhaul; new boss monsters |
| v0.3.0 | 2015 | Wand system overhaul |
| v0.4.0 | 2016 | Desktop/PC platform support |
| v0.6.0 | 2017 | New room types; glyph overhaul |
| v0.7.5 | 2019 | Tengu boss fight overhauled |
| v0.8.0 | 2020 | Yog-Dzewa boss fight overhauled |
| v0.9.0 | 2020 | Talent system introduced |
| v1.2.0 | 2022 | Full keyboard/keybind support |
| v2.0.0 | 2022 | Duelist — 5th hero class added |
| v3.0.0 | 2025 | Cleric — 6th hero class added |
| v3.2.0 | 2025 | Thrown weapons rebuilt as upgradeable sets |
| v3.3.5 | 2026 | Latest upstream release |
