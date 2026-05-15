package io.github.heroes.magic;

import io.github.heroes.model.Hero;
import io.github.heroes.model.UnitStack;

public class MagicArrowSpell extends Spell {
    private static final int BASE_DAMAGE = 30;
    private static final int DAMAGE_PER_SPELL_POWER = 10;

    public MagicArrowSpell() {
        super("Magic Arrow", 5);
    }

    @Override
    public void cast(Hero caster, UnitStack target) {
        if (!canCast(caster)) {
            throw new IllegalStateException("Not enough mana");
        }

        int damage = BASE_DAMAGE + caster.getSpellPower() * DAMAGE_PER_SPELL_POWER;

        target.takeDamage(damage);
        caster.spendMana(getManaCost());
    }
}
