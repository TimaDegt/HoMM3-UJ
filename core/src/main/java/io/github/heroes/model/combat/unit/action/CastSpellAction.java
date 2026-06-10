package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.magic.Spell;
import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.List;

public class CastSpellAction implements BattleAction {
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

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (!target.isAlive()) throw new IllegalStateException("Cannot cast spell on dead unit");
        if (!spell.canCast(caster)) throw new IllegalStateException("Not enough mana to cast spell");

        ActionResult result = spell.cast(caster, state.getActiveUnit(), target);
        if (!result.successful()) throw new IllegalStateException("Spell cast failed");
        return result.events();
    }

    @Override
    public boolean endsTurn() {
        return false;
    }

}
