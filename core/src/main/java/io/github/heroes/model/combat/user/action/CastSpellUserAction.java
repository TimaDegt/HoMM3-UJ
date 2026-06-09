package io.github.heroes.model.combat.user.action;

import io.github.heroes.model.magic.Spell;
import io.github.heroes.model.state.Position;

public class CastSpellUserAction implements UserAction {
    private final Spell spell;
    private final Position targetPosition;

    public CastSpellUserAction(Spell spell, Position targetPosition) {
        if (spell == null) throw new IllegalArgumentException("Spell cannot be null");
        if (targetPosition == null) throw new IllegalArgumentException("Target position cannot be null");

        this.spell = spell;
        this.targetPosition = targetPosition;
    }

    public Spell getSpell() {
        return spell;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }
}
