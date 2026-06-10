package io.github.heroes.model.magic;

import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.unit.stack.UnitStack;

public class ResurrectionSpell extends Spell {
    private final int baseHp;
    private final int hpPerSpellPower;

    public ResurrectionSpell(String name, int manaCost, int lvl, MagicSchool school, int baseHp, int hpPerSpellPower) {
        super(name, manaCost, lvl, school);
        this.baseHp = baseHp;
        this.hpPerSpellPower = hpPerSpellPower;
    }

    @Override
    protected void applyEffect(Hero caster, UnitStack target) {
        int restoreHp = baseHp + (caster.getSpellPower() * hpPerSpellPower);

        target.resurrect(restoreHp);
    }
}
