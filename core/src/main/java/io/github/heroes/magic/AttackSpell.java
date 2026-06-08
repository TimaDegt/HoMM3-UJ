package io.github.heroes.magic;

import io.github.heroes.model.Hero;
import io.github.heroes.model.UnitStack;

import java.util.List;

public class AttackSpell extends Spell {
    private final int[] baseDamage;
    private final int damagePerSpellPower;

    public AttackSpell(String name, int manaCost, int[] damage, int spellPower, int lvl, MagicSchool school) {
        super(name, manaCost, lvl, school);

        if (damage[0] < 0 || damage[1] < 0 || damage[2] < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        this.baseDamage = damage;
        this.damagePerSpellPower = spellPower;
    }

    public int getDamage(Hero caster) {
        return baseDamage[caster.getMagicSchoolLevel(this.school)]+caster.getSpellPower()*damagePerSpellPower;
    }

    @Override
    public void cast(Hero caster, UnitStack target) {
        if (!canCast(caster)) {
            return;
        }
        caster.spendMana(getManaCost(caster));
        target.takeDamage(getDamage(caster));
    }
}
