package io.github.heroes.magic;

import io.github.heroes.model.Hero;
import io.github.heroes.model.UnitStack;

public class DamageRollModifierSpell extends Spell {
    private final boolean isBless;

    public DamageRollModifierSpell(String name, int manaCost, int lvl, MagicSchool school, boolean isBless) {
        super(name, manaCost, lvl, school);
        this.isBless = isBless;
    }

    @Override
    public void cast(Hero caster, UnitStack target) {
        if (!canCast(caster)) return;
        caster.spendMana(getManaCost(caster));

        if (isBless) {
            target.lockDamageToMaximum(); // Bless
        } else {
            target.lockDamageToMinimum(); // Curse
        }
    }
}
