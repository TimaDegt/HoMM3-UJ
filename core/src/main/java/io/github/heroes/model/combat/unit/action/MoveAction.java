package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.combat.BattlePathFinder;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.List;

public class MoveAction implements BattleAction {
    private final UnitStack unit;
    private final Position targetPosition;

    public MoveAction(UnitStack unit, Position targetPosition) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (targetPosition == null) throw new IllegalArgumentException("Target position cannot be null");
        this.unit = unit;
        this.targetPosition = targetPosition;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (!unit.isAlive()) throw new IllegalStateException("Dead unit cannot move");
        if (state.getActiveUnit() != unit) throw new IllegalStateException("Only active unit can move");
        if (!state.getField().isInside(targetPosition)) throw new IllegalStateException("Target position is outside the battlefield");

        if (!BattlePathFinder.canReach(state, unit, targetPosition)) return List.of();
        Position startPosition = unit.getPosition();
        List<Position> path = BattlePathFinder.findPath(state, startPosition, targetPosition);
        unit.changePosition(targetPosition);
        return List.of(new BattleEvent.UnitMoved(
            unit,
            startPosition,
            targetPosition,
            path
        ));
    }
}
