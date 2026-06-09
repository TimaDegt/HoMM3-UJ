package io.github.heroes.model.magic;

public enum Spells {

    MAGIC_ARROW(new AttackSpell(
        "Magic Arrow", 5, new int[]{10, 10, 20, 30}, 10, 1, MagicSchool.WIND
    )),
    HASTE(new BuffSpell(
        "Haste", 6, 1, MagicSchool.WIND,
        null, 0, null, null, new int[]{3, 3, 5, 5}
    )),
    SLOW(new BuffSpell(
        "Slow", 6, 1, MagicSchool.EARTH,
        null, 0, null, null, new int[]{-3, -3, -6, -6}
    )),
    BLOODLUST(new BuffSpell(
        "Bloodlust", 5, 1, MagicSchool.FIRE,
        null, 0, new int[]{3, 3, 6, 6}, null, null
    )),
    WEAKNESS(new BuffSpell(
        "Weakness", 5, 1, MagicSchool.WATER,
        null, 0, new int[]{-3, -3, -6, -6}, null, null
    )),
    STONE_SKIN(new BuffSpell(
        "Stone Skin", 5, 1, MagicSchool.EARTH,
        null, 0, null, new int[]{3, 3, 6, 6}, null
    )),
    DISRUPTING_RAY(new BuffSpell(
        "Disrupting Ray", 6, 2, MagicSchool.WIND,
        null, 0, null, new int[]{-3, -3, -4, -5}, null
    )),
    SHIELD(new BuffSpell(
        "Shield", 5, 1, MagicSchool.EARTH,
        null, 0, null, new int[]{3, 3, 6, 6}, null
    )),
    PRAYER(new BuffSpell(
        "Prayer", 16, 4, MagicSchool.WATER,
        null, 0, new int[]{2, 2, 4, 4}, new int[]{2, 2, 4, 4}, new int[]{2, 2, 4, 4}
    )),

    BLESS(new DamageRollModifierSpell(
        "Bless", 5, 1, MagicSchool.WATER, true
    )),
    CURSE(new DamageRollModifierSpell(
        "Curse", 6, 1, MagicSchool.FIRE, false
    )),
    DISPEL(new DispelSpell(
        "Dispel", 5, 1, MagicSchool.WATER,
        null, 0
    )),
    CURE(new DispelSpell(
        "Cure", 6, 1, MagicSchool.WATER,
        new int[]{10, 10, 20, 30}, 5
    )),

    RESURRECTION(new ResurrectionSpell(
        "Resurrection", 20, 4, MagicSchool.EARTH, 40, 50
    ));

    private final Spell spell;
    Spells(Spell spell) {
        this.spell = spell;
    }

    public Spell getSpell() {
        return spell;
    }
}
