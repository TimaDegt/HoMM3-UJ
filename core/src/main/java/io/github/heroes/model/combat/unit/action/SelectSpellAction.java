package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.magic.Spell;
import io.github.heroes.model.state.BattleState;

import java.util.List;

public class SelectSpellAction implements BattleAction {
    private final int spellIndex;
    private final Spell spell;

    public SelectSpellAction(int spellIndex, Spell spell) {
        if (spellIndex < 0) throw new IllegalArgumentException("Spell index cannot be negative");
        if (spell == null) throw new IllegalArgumentException("Spell cannot be null");

        this.spellIndex = spellIndex;
        this.spell = spell;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        state.selectSpell(spell);
        return List.of(new BattleEvent.SpellSelected(spellIndex));
    }

    @Override
    public boolean endsTurn() {
        return false;
    }
}
