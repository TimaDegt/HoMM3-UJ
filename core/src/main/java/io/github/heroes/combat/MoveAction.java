package io.github.heroes.combat;

import io.github.heroes.model.BattleState;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.model.anim.AnimParams;

import java.util.List;

public class MoveAction implements BattleAction {
    private final UnitStack unit;
    private final Position targetPosition;

    public MoveAction(UnitStack unit, Position targetPosition) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        if (targetPosition == null) {
            throw new IllegalArgumentException("Target position cannot be null");
        }

        this.unit = unit;
        this.targetPosition = targetPosition;
    }

    @Override
    public void execute(BattleState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        if (!unit.isAlive()) {
            throw new IllegalStateException("Dead unit cannot move");
        }
        if (state.getActiveUnit() != unit) {
            throw new IllegalStateException("Only active unit can move");
        }
        if (!state.getField().isInside(targetPosition)) {
            throw new IllegalStateException("Target position is outside the battlefield");
        }

        List<Position> path = BattlePathFinder.findPath(state, unit.getPosition(), targetPosition);
        unit.initiateMovement(path);
//        unit.startAnimation(AnimParams.AnimType.MOVE);
//        for (Position nextPosition : path) {
//            unit.changePosition(nextPosition);
//        }
    }
}
