package io.github.heroes.magic;

import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.UnitStack;

public abstract class Spell {
    private final int lvl;
    public final MagicSchool school;
    private final String name;
    private final int manaCost;

    protected Spell(String name, int manaCost, int lvl, MagicSchool school) {
        if (manaCost < 0) {
            throw new IllegalArgumentException("Mana cost cannot be negative");
        }
        if (lvl <= 0 || lvl > 5) {
            throw new IllegalArgumentException("Level must be between 1 and 5");
        }
        this.school = school;
        this.lvl = lvl;
        this.name = name;
        this.manaCost = manaCost;
    }
    public int getLvl() {
        return lvl;
    }
    public MagicSchool getSchool() {
        return school;
    }

    public String getName() {
        return name;
    }

    public int getManaCost(Hero caster) {
        return manaCost - lvl * (caster.getMagicSchoolLevel(school)>0?1:0);
    }

    public boolean canCast(Hero caster) {
        return caster.getMana() >= manaCost;
    }

    public abstract void cast(Hero caster, UnitStack target);
}
