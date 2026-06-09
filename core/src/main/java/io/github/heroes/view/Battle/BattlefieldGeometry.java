package io.github.heroes.view.Battle;

import com.badlogic.gdx.math.Vector2;
import io.github.heroes.model.snapshot.BattleSnapshot;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.Position;

public class BattlefieldGeometry {
    public static Vector2 positionToScreen(Position position) {
        float x = BattleViewConfig.FIELD_START_X + position.x() * BattleViewConfig.HEX_WIDTH;
        float y = BattleViewConfig.FIELD_START_Y + position.y() * BattleViewConfig.HEX_HEIGHT * 0.75f;

        if (position.y() % 2 == 1) {
            x += BattleViewConfig.HEX_WIDTH / 2;
        }

        return new Vector2(x, y);
    }

    public static Position screenToPosition(
        float screenX,
        float screenY,
        BattleSnapshot battle
    ) {
        Position closestPosition = null;
        float closestDistance = Float.MAX_VALUE;

        for (int row = 0; row < battle.fieldHeight(); row++) {
            for (int col = 0; col < battle.fieldWidth(); col++) {
                Position position = new Position(col, row);
                Vector2 center = positionToScreen(position);
                float distance = center.dst(screenX, screenY);

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPosition = position;
                }
            }
        }

        if (closestDistance <= BattleViewConfig.HEX_SIZE) {
            return closestPosition;
        }

        return null;
    }

    public static Position findNearestNeighbor(
        Position targetPosition,
        float screenX,
        float screenY,
        BattleSnapshot battle
    ) {
        UnitSnapshot activeUnit = battle.activeUnit();
        if (activeUnit == null) return null;

        Position nearestPosition = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Position neighbor : targetPosition.neighbors()) {
            if (!battle.isInside(neighbor)) continue;
            if (!activeUnit.position().equals(neighbor) && battle.isPositionOccupied(neighbor)) continue;

            float distance = positionToScreen(neighbor).dst(screenX, screenY);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPosition = neighbor;
            }
        }

        return nearestPosition;
    }
}
