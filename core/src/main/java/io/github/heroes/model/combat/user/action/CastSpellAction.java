package io.github.heroes.model.combat.user.action;

import io.github.heroes.magic.Spell;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.UnitStack;

public class CastSpellAction implements UserAction {
    private final Hero caster;
    private final Spell spell;
    private final UnitStack target;

    public CastSpellAction(Hero caster, Spell spell, UnitStack target) {
        if (caster == null) throw new IllegalArgumentException("Caster cannot be null");
        if (spell == null) throw new IllegalArgumentException("Spell cannot be null");
        if (target == null) throw new IllegalArgumentException("Target cannot be null");

        this.caster = caster;
        this.spell = spell;
        this.target = target;
    }

    public Hero getCaster() {
        return caster;
    }

    public Spell getSpell() {
        return spell;
    }

    public UnitStack getTarget() {
        return target;
    }
}
