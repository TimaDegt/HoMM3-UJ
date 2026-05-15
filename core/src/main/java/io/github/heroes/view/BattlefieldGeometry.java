package io.github.heroes.view;

import com.badlogic.gdx.math.Vector2;
import io.github.heroes.model.BattleField;
import io.github.heroes.model.Position;

public class BattlefieldGeometry {
    public Vector2 positionToScreen(Position position) {
        float x = BattleViewConfig.FIELD_START_X + position.x() * BattleViewConfig.HEX_WIDTH;
        float y = BattleViewConfig.FIELD_START_Y + position.y() * BattleViewConfig.HEX_HEIGHT * 0.75f;

        if (position.y() % 2 == 1) {
            x += BattleViewConfig.HEX_WIDTH / 2;
        }

        return new Vector2(x, y);
    }

    public Position screenToPosition(float screenX, float screenY, BattleField field) {
        Position closestPosition = null;
        float closestDistance = Float.MAX_VALUE;

        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
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

    public Position[] getNeighbors(Position position) {
        int x = position.x();
        int y = position.y();

        if (y % 2 == 0) {
            return new Position[] {
                new Position(x + 1, y),
                new Position(x - 1, y),
                new Position(x, y + 1),
                new Position(x - 1, y + 1),
                new Position(x, y - 1),
                new Position(x - 1, y - 1)
            };
        }

        return new Position[] {
            new Position(x + 1, y),
            new Position(x - 1, y),
            new Position(x + 1, y + 1),
            new Position(x, y + 1),
            new Position(x + 1, y - 1),
            new Position(x, y - 1)
        };
    }
}
