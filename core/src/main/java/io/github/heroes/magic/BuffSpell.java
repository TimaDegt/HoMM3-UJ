package io.github.heroes.magic;

import io.github.heroes.model.Hero;
import io.github.heroes.model.UnitStack;

public class BuffSpell extends Spell {
    private final int[] baseHeal;
    private final int healPerSpellPower;

    private final int[] attackBonus;
    private final int[] defenseBonus;
    private final int[] speedBonus;

    public BuffSpell(String name, int manaCost, int lvl, MagicSchool school,
                     int[] baseHeal, int healPerSpellPower,
                     int[] attackBonus, int[] defenseBonus, int[] speedBonus) {
        super(name, manaCost, lvl, school);
        this.baseHeal = baseHeal;
        this.healPerSpellPower = healPerSpellPower;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.speedBonus = speedBonus;
    }

    @Override
    public void cast(Hero caster, UnitStack target) {
        if (!canCast(caster)) return;
        caster.spendMana(getManaCost(caster));

        int schoolLevel = caster.getMagicSchoolLevel(this.school);

        if (baseHeal != null) target.heal(baseHeal[schoolLevel] + caster.getSpellPower() * healPerSpellPower);
        if (attackBonus != null) target.addAttackBuff(attackBonus[schoolLevel]);
        if (defenseBonus != null) target.addDefenseBuff(defenseBonus[schoolLevel]);
        if (speedBonus != null) target.addSpeedBuff(speedBonus[schoolLevel]);
    }
}
