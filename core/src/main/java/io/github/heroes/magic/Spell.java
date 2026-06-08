package io.github.heroes.magic;

import io.github.heroes.model.Hero;
import io.github.heroes.model.UnitStack;

public abstract class Spell {
    private final String name;
    private final int manaCost;

    protected Spell(String name, int manaCost) {
        if (manaCost < 0) {
            throw new IllegalArgumentException("Mana cost cannot be negative");
        }

        this.name = name;
        this.manaCost = manaCost;
    }

    public String getName() {
        return name;
    }

    public int getManaCost() {
        return manaCost;
    }

    public boolean canCast(Hero caster) {
        return caster.getMana() >= manaCost;
    }

    public abstract void cast(Hero caster, UnitStack target);

    public int getLvl() {}
    public int getManaCost(Hero caster) {}
    public MagicSchool getMagicSchool() {}
    public

}
