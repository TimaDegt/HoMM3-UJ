package io.github.heroes.model.combat.user.action;

import io.github.heroes.magic.Spell;
import io.github.heroes.model.state.unit.stack.UnitStack;

public class CastSpellUserAction implements UserAction {
    private final Spell spell;
    private final UnitStack target;

    public CastSpellUserAction(Spell spell, UnitStack target) {
        if (spell == null) throw new IllegalArgumentException("Spell cannot be null");
        if (target == null) throw new IllegalArgumentException("Target cannot be null");

        this.spell = spell;
        this.target = target;
    }

    public Spell getSpell() {
        return spell;
    }

    public UnitStack getTarget() {
        return target;
    }
}
