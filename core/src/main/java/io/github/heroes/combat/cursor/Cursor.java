package io.github.heroes.combat.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.BattlePathFinder;
import io.github.heroes.control.BattleScreenController;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.view.BattlefieldGeometry;

public class Cursor {
    private static UnitStack getHoveredUnit(float mouseX, float mouseY, BattleController battleController) {
        Position converted = BattlefieldGeometry.screenToPosition(mouseX, mouseY, battleController.getState().getField());
        return battleController.findUnitAt(converted);
    }

    public static CursorType getCustomCursor(BattleController battleController) {
        if (battleController == null) return CursorType.DEFAULT;
        if (battleController.getState().isFinished()) return CursorType.DEFAULT;
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        UnitStack hoveredUnit = getHoveredUnit(mouseX, mouseY, battleController);
        UnitStack activeUnit = battleController.getActiveUnit();

        if (hoveredUnit == null) return CursorType.DEFAULT;
        if (hoveredUnit.getOwner() == activeUnit.getOwner()) {
            return CursorType.NOPE;
        }

        Position position = BattleScreenController.findNearestAttackPosition(hoveredUnit.getPosition(),mouseX,mouseY,battleController);
        if (position == null) return CursorType.DEFAULT;
        if (!BattlePathFinder.canReach(battleController.getState(), activeUnit, position ) ) return CursorType.DEFAULT;
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
