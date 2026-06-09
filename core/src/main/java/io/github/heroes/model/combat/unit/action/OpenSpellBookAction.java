package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.spellBook.SpellBook;
import io.github.heroes.model.state.BattleState;

import java.util.List;

public class OpenSpellBookAction implements BattleAction {
    private final SpellBook spellBook;

    public OpenSpellBookAction(SpellBook spellBook) {
        if (spellBook == null) throw new IllegalArgumentException("Spell book cannot be null");
        this.spellBook = spellBook;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        return List.of(new BattleEvent.OpenSpellBook());
    }

    @Override
    public boolean endsTurn() {
        return false;
    }
}
