package io.github.heroes.control;

import com.badlogic.gdx.math.Vector2;
import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.BattlePathFinder;
import io.github.heroes.combat.MoveAction;
import io.github.heroes.combat.MoveAndAttackAction;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.view.BattlefieldGeometry;

public class BattleScreenController {
    private final BattleController battleController;
    private final BattlefieldGeometry battlefieldGeometry;
    private final BattlePathFinder battlePathFinder;

    public BattleScreenController(
        BattleController battleController,
        BattlefieldGeometry battlefieldGeometry,
        BattlePathFinder battlePathFinder
    ) {
        this.battleController = battleController;
        this.battlefieldGeometry = battlefieldGeometry;
        this.battlePathFinder = battlePathFinder;
    }

    public boolean handleLeftBattlefieldClick(float x, float y) {
        if (battleController.getState().isFinished()) {
            return false;
        }

        Position clickedPosition = getBattlefieldPosition(x, y);
        if (clickedPosition == null) {
            return false;
        }

        UnitStack clickedUnit = battleController.findUnitAt(clickedPosition);
        if (clickedUnit != null) {
            return handleUnitClick(clickedUnit, x, y);
        }

        UnitStack activeUnit = battleController.getActiveUnit();
        if (canReach(activeUnit, clickedPosition)) {
            battleController.performAction(new MoveAction(activeUnit, clickedPosition));
            return true;
        }

        return false;
    }

    public UnitStack findUnitUnderCursor(float x, float y) {
        Position clickedPosition = getBattlefieldPosition(x, y);
        if (clickedPosition == null) {
            return null;
        }

        return battleController.findUnitAt(clickedPosition);
    }

    private boolean handleUnitClick(UnitStack clickedUnit, float x, float y) {
        UnitStack activeUnit = battleController.getActiveUnit();

        if (clickedUnit.getOwner() == activeUnit.getOwner()) {
            return false;
        }

        Position attackPosition = findNearestAttackPosition(clickedUnit.getPosition(), x, y);
        if (attackPosition == null || !canReach(activeUnit, attackPosition)) {
            return false;
        }

        battleController.performAction(new MoveAndAttackAction(activeUnit, attackPosition, clickedUnit));
        return true;
    }

    private Position getBattlefieldPosition(float x, float y) {
        return battlefieldGeometry.screenToPosition(
            x,
            y,
            battleController.getState().getField()
        );
    }

    private Position findNearestAttackPosition(Position targetPosition, float clickX, float clickY) {
        Position nearestPosition = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Position neighbor : battlefieldGeometry.getNeighbors(targetPosition)) {
            if (!battleController.getState().getField().isInside(neighbor)) {
                continue;
            }
            if (battleController.isPositionOccupied(neighbor)
                && !neighbor.equals(battleController.getActiveUnit().getPosition())) {
                continue;
            }

            Vector2 center = battlefieldGeometry.positionToScreen(neighbor);
            float distance = center.dst(clickX, clickY);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPosition = neighbor;
            }
        }

        return nearestPosition;
    }

    private boolean canReach(UnitStack unit, Position targetPosition) {
        return battlePathFinder.canReach(battleController.getState(), unit, targetPosition);
    }
}
