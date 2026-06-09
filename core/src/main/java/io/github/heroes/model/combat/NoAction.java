package io.github.heroes.model.combat;

import io.github.heroes.magic.Spell;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.UnitStack;

import java.util.ArrayList;
import java.util.List;

public class NoAction implements BattleAction {
    public NoAction() {}

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        List<BattleEvent> events = new ArrayList<>();
        return events;
    }
}
