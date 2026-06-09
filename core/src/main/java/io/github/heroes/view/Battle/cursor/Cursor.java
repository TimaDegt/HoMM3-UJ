package io.github.heroes.view.Battle.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.control.BattleController;
import io.github.heroes.model.combat.BattleActionPreview;
import io.github.heroes.model.snapshot.BattleSnapshot;
import io.github.heroes.model.state.Position;
import io.github.heroes.view.Battle.BattlefieldGeometry;

import static io.github.heroes.view.Battle.BattleViewConfig.ACTION_PANEL_BASE_HEIGHT;
import static io.github.heroes.view.Battle.BattleViewConfig.ACTION_PANEL_SCALE;

public class Cursor {
    public static CursorType getCustomCursor(BattleController controller) {
        if (controller == null) return CursorType.DEFAULT;

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        if (mouseY <= ACTION_PANEL_BASE_HEIGHT * ACTION_PANEL_SCALE) return CursorType.NONE;

        BattleSnapshot battle = controller.getBattleSnapshot();
        Position targetPosition = BattlefieldGeometry.screenToPosition(mouseX, mouseY, battle);
        if (targetPosition == null) return CursorType.DEFAULT;

        Position attackFromPosition = BattlefieldGeometry.findNearestNeighbor(
            targetPosition,
            mouseX,
            mouseY
        );
        BattleActionPreview preview = controller.previewAction(
            targetPosition,
            attackFromPosition
        );

        return switch (preview) {
            case MOVE -> CursorType.MOVE;
            case RANGED_ATTACK -> CursorType.RANGER;
            case RANGED_ATTACK_UNAVAILABLE -> CursorType.RANGERBROKEN;
            case ALLY -> CursorType.QUESTION;
            case INVALID -> CursorType.NOPE;
            case MELEE_ATTACK -> getDirectionalAttackCursor(
                attackFromPosition,
                targetPosition
            );
        };
    }

    private static CursorType getDirectionalAttackCursor(
        Position attackFromPosition,
        Position targetPosition
    ) {
        if (attackFromPosition == null) return CursorType.NOPE;

        Vector2 attackPosition = BattlefieldGeometry.positionToScreen(attackFromPosition);
        Vector2 target = BattlefieldGeometry.positionToScreen(targetPosition);
        if (attackPosition.y == target.y) {
            if (attackPosition.x > target.x) return CursorType.ATTACKL;
            if (attackPosition.x < target.x) return CursorType.ATTACKR;
        }
        if (attackPosition.x > target.x) {
            if (attackPosition.y < target.y) return CursorType.ATTACKLU;
            if (attackPosition.y > target.y) return CursorType.ATTACKLD;
        }
        if (attackPosition.x < target.x) {
            if (attackPosition.y < target.y) return CursorType.ATTACKRU;
            if (attackPosition.y > target.y) return CursorType.ATTACKRD;
        }
        return CursorType.NOPE;
    }
}
