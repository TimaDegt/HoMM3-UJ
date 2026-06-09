package io.github.heroes.model.combat;

import io.github.heroes.model.spellBook.SpellBook;
import io.github.heroes.model.state.BattleState;

import java.util.ArrayList;
import java.util.List;

public class OpenSpellBookAction implements BattleAction {
    SpellBook spellBook;
    public OpenSpellBookAction(SpellBook book) {
        this.spellBook = book;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        List<BattleEvent> events = new ArrayList<>();
        events.add(new BattleEvent.OpenSpellBook(
            spellBook
        ));
        return events;
    }
}

