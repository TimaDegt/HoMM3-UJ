package io.github.heroes.model.magic;

import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.unit.stack.UnitStack;

public class MagicArrowSpell extends Spell {
    private static final int BASE_DAMAGE = 30;
    private static final int DAMAGE_PER_SPELL_POWER = 10;

    public MagicArrowSpell() {
        super("Magic Arrow", 5, 1, MagicSchool.WIND);
    }

    @Override
    public void cast(Hero caster, UnitStack target) {
        if (!canCast(caster)) {
            throw new IllegalStateException("Not enough mana");
        }

        int damage = BASE_DAMAGE + caster.getSpellPower() * DAMAGE_PER_SPELL_POWER;

        target.takeDamage(damage);
        caster.spendMana(getManaCost(caster));
    }
}
