package io.github.heroes.combat;

import io.github.heroes.model.BattleState;
import io.github.heroes.model.UnitStack;

public class WaitAction implements BattleAction {
    private final UnitStack unit;

    public WaitAction(UnitStack unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }

        this.unit = unit;
    }

    @Override
    public void execute(BattleState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        if (!unit.isAlive()) {
            throw new IllegalStateException("Dead unit cannot wait");
        }

        if (state.getActiveUnit() != unit) {
            throw new IllegalStateException("Only active unit can wait");
        }

    }
}
