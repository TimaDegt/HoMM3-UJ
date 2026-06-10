package io.github.heroes.model.magic;

import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.unit.stack.UnitStack;

public class DamageRollModifierSpell extends Spell {
    private final boolean isBless;

    public DamageRollModifierSpell(String name, int manaCost, int lvl, MagicSchool school, boolean isBless) {
        super(name, manaCost, lvl, school);
        this.isBless = isBless;
    }

    @Override
    protected void applyEffect(Hero caster, UnitStack target) {
        if (isBless) {
            target.lockDamageToMaximum(); // Bless
        } else {
            target.lockDamageToMinimum(); // Curse
        }
    }
}
