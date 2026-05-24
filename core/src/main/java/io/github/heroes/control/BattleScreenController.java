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

    public BattleScreenController(){
    }

    public static boolean handleLeftBattlefieldClick(float x, float y, BattleController battleController) {
        if (battleController.getState().isFinished()) {
            return false;
        }

        Position clickedPosition = BattlefieldGeometry.screenToPosition(
            x,
            y,
            battleController.getState().getField()
        );
        if (clickedPosition == null) {
            return false;
        }

        UnitStack clickedUnit = battleController.findUnitAt(clickedPosition);
        if (clickedUnit != null) {
            return handleUnitClick(clickedUnit, x, y, battleController);
        }

        UnitStack activeUnit = battleController.getActiveUnit();
        if (canReach(activeUnit, clickedPosition, battleController)) {
            battleController.performAction(new MoveAction(activeUnit, clickedPosition));
            return true;
        }

        return false;
    }

    public static UnitStack findUnitUnderCursor(float x, float y, BattleController battleController) {
        Position clickedPosition = BattlefieldGeometry.screenToPosition(
            x,
            y,
            battleController.getState().getField()
        );
        if (clickedPosition == null) {
            return null;
        }
        return battleController.findUnitAt(clickedPosition);
    }

    private static boolean handleUnitClick(UnitStack clickedUnit, float x, float y, BattleController battleController) {
        UnitStack activeUnit = battleController.getActiveUnit();

        if (clickedUnit.getOwner() == activeUnit.getOwner()) {
            return false;
        }

        Position attackPosition = findNearestAttackPosition(clickedUnit.getPosition(), x, y, battleController);
        if (attackPosition == null || !canReach(activeUnit, attackPosition, battleController)) {
            return false;
        }

        battleController.performAction(new MoveAndAttackAction(activeUnit, attackPosition, clickedUnit));
        return true;
    }

    public static Position findNearestAttackPosition(Position targetPosition, float clickX, float clickY, BattleController battleController) {
        Position nearestPosition = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Position neighbor : BattlefieldGeometry.getNeighbors(targetPosition)) {
            if (!battleController.getState().getField().isInside(neighbor)) {
                continue;
            }

            Vector2 center = BattlefieldGeometry.positionToScreen(neighbor);
            float distance = center.dst(clickX, clickY);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPosition = neighbor;
            }
        }
        if (nearestPosition == null) return null;
        if (battleController.isPositionOccupied(nearestPosition)
                && !nearestPosition.equals(battleController.getActiveUnit().getPosition())) {
            return null;
        }
        return nearestPosition;
    }

    private static boolean canReach(UnitStack unit, Position targetPosition, BattleController battleController) {
        return BattlePathFinder.canReach(battleController.getState(), unit, targetPosition);
    }
}
