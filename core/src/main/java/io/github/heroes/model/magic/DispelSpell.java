package io.github.heroes.model.magic;

import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.unit.stack.UnitStack;

public class DispelSpell extends Spell {
    private final int[] baseHeal;
    private final int healPerSpellPower;

    public DispelSpell(String name, int manaCost, int lvl, MagicSchool school, int[] baseHeal, int healPerSpellPower) {
        super(name, manaCost, lvl, school);
        this.baseHeal = baseHeal;
        this.healPerSpellPower = healPerSpellPower;
    }

    @Override
    protected void applyEffect(Hero caster, UnitStack target) {
        target.clearAllBuffsAndDebuffs();

        if (baseHeal != null) {
            int healAmount = baseHeal[caster.getMagicSchoolLevel(this.school)] + caster.getSpellPower() * healPerSpellPower;
            target.heal(healAmount);
        }
    }
}
