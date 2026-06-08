package io.github.heroes.model.combat;

import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.UnitStack;

import java.util.List;

public class WaitAction implements BattleAction {
    private final UnitStack unit;

    public WaitAction(UnitStack unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");

        this.unit = unit;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (!unit.isAlive()) throw new IllegalStateException("Dead unit cannot wait");
        if (state.getActiveUnit() != unit) throw new IllegalStateException("Only active unit can wait");

        return List.of(new BattleEvent.UnitWaited(unit));
    }
}
