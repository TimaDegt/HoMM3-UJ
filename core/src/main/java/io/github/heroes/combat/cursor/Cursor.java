package io.github.heroes.combat.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.combat.BattlePathFinder;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;
import io.github.heroes.view.Battle.BattlefieldGeometry;

import static io.github.heroes.view.Battle.BattleViewConfig.ACTION_PANEL_BASE_HEIGHT;
import static io.github.heroes.view.Battle.BattleViewConfig.ACTION_PANEL_SCALE;

public class Cursor {
    private static UnitStack getHoveredUnit(float mouseX, float mouseY, BattleEngine battleEngine) {
        Position converted = BattlefieldGeometry.screenToPosition(mouseX, mouseY, battleEngine.getState().getField());
        return battleEngine.findUnitAt(converted);
    }

    public static CursorType getCustomCursor(BattleEngine battleEngine) {
        if (battleEngine == null) return CursorType.DEFAULT;
        if (battleEngine.getState().isFinished()) return CursorType.DEFAULT;
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        if (mouseY <= ACTION_PANEL_BASE_HEIGHT * ACTION_PANEL_SCALE) return CursorType.NONE;

        UnitStack hoveredUnit = getHoveredUnit(mouseX, mouseY, battleEngine);
        UnitStack activeUnit = battleEngine.getActiveUnit();

        if (hoveredUnit == null) return CursorType.DEFAULT;
        if (hoveredUnit.getOwner() == activeUnit.getOwner()) {
            return CursorType.NOPE;
        }

        Position position = BattlefieldGeometry.findNearestNeighbor(
            hoveredUnit.getPosition(),
            mouseX,
            mouseY,
            battleEngine.getState().getField()
        );
        if (position == null) return CursorType.DEFAULT;
        if (battleEngine.isPositionOccupied(position)
                && !position.equals(activeUnit.getPosition())) return CursorType.DEFAULT;
        if (!BattlePathFinder.canReach(battleEngine.getState(), activeUnit, position ) ) return CursorType.DEFAULT;
        Vector2 pos = BattlefieldGeometry.positionToScreen(position);
        Vector2 pos2 = BattlefieldGeometry.positionToScreen(hoveredUnit.getPosition());
        if (pos.y == pos2.y) {
            if (pos.x > pos2.x) return CursorType.ATTACKL;
            if (pos.x < pos2.x) return CursorType.ATTACKR;
        }
        if (pos.x > pos2.x) {
            if (pos.y < pos2.y) return CursorType.ATTACKLU;
            if (pos.y > pos2.y) return CursorType.ATTACKLD;
        }
        if (pos.x < pos2.x) {
            if (pos.y < pos2.y) return CursorType.ATTACKRU;
            if (pos.y > pos2.y) return CursorType.ATTACKRD;
        }
        return CursorType.DEFAULT;
    }
}
