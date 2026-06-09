package io.github.heroes.combat.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.control.BattleController;
import io.github.heroes.model.snapshot.BattleSnapshot;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.Position;
import io.github.heroes.view.Battle.BattlefieldGeometry;

import static io.github.heroes.view.Battle.BattleViewConfig.ACTION_PANEL_BASE_HEIGHT;
import static io.github.heroes.view.Battle.BattleViewConfig.ACTION_PANEL_SCALE;

public class Cursor {
    public static CursorType getCustomCursor(BattleController controller) {
        if (controller == null) return CursorType.DEFAULT;

        BattleSnapshot battle = controller.getBattleSnapshot();
        if (battle.isFinished()) return CursorType.DEFAULT;

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        if (mouseY <= ACTION_PANEL_BASE_HEIGHT * ACTION_PANEL_SCALE) return CursorType.NONE;

        Position hoveredPosition = BattlefieldGeometry.screenToPosition(mouseX, mouseY, battle);
        UnitSnapshot hoveredUnit = battle.findUnitAt(hoveredPosition);
        UnitSnapshot activeUnit = battle.activeUnit();
        if (activeUnit == null) return CursorType.DEFAULT;

        if (hoveredUnit == null) {
            if (!controller.canActiveUnitReach(hoveredPosition)) return CursorType.NOPE;
            return CursorType.MOVE;
        }
        if (hoveredUnit.owner() == activeUnit.owner()) return CursorType.QUESTION;
        if (controller.activeUnitSupportsRangedAttack()) {
            return controller.canActiveUnitAttackWithoutMoving(hoveredPosition)
                ? CursorType.RANGER
                : CursorType.RANGERBROKEN;
        }

        Position position = BattlefieldGeometry.findNearestNeighbor(
            hoveredUnit.position(),
            mouseX,
            mouseY,
            battle
        );
        if (position == null) return CursorType.DEFAULT;
        if (battle.isPositionOccupied(position) && !position.equals(activeUnit.position())) {
            return CursorType.DEFAULT;
        }
        if (!controller.canActiveUnitReach(position)) return CursorType.NOPE;

        Vector2 attackPosition = BattlefieldGeometry.positionToScreen(position);
        Vector2 targetPosition = BattlefieldGeometry.positionToScreen(hoveredUnit.position());
        if (attackPosition.y == targetPosition.y) {
            if (attackPosition.x > targetPosition.x) return CursorType.ATTACKL;
            if (attackPosition.x < targetPosition.x) return CursorType.ATTACKR;
        }
        if (attackPosition.x > targetPosition.x) {
            if (attackPosition.y < targetPosition.y) return CursorType.ATTACKLU;
            if (attackPosition.y > targetPosition.y) return CursorType.ATTACKLD;
        }
        if (attackPosition.x < targetPosition.x) {
            if (attackPosition.y < targetPosition.y) return CursorType.ATTACKRU;
            if (attackPosition.y > targetPosition.y) return CursorType.ATTACKRD;
        }
        return CursorType.DEFAULT;
    }
}
