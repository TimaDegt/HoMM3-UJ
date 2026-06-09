package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.List;

public class DefendAction implements BattleAction {
    private final UnitStack unit;

    public DefendAction(UnitStack unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");

        this.unit = unit;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (!unit.isAlive()) throw new IllegalStateException("Dead unit cannot defend");

        unit.setDefending(true);
        return List.of(new BattleEvent.UnitDefended(unit));
    }
}
