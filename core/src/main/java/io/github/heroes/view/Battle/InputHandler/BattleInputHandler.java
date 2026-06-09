package io.github.heroes.view.Battle.InputHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.control.BattleController;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;
import io.github.heroes.view.Battle.BattleScreen;
import io.github.heroes.view.Battle.BattlefieldGeometry;
import io.github.heroes.view.Battle.UnitInfoPopup;


public class BattleInputHandler extends InputAdapter {
    private final BattleController battleController;
    private final UnitInfoPopup unitInfoPopup;
    private ActionResult actionResult;
    private final Runnable exitBattle;
    private boolean battleInputEnabled = true;
    private final BattleScreen battleScreen;
    private final BattleEngine battleEngine;

    public BattleInputHandler(
        BattleController battleController,
        BattleEngine battleEngine,
        UnitInfoPopup unitInfoPopup,
        BattleScreen battleScreen,
        Runnable exitBattle
    ) {
        this.battleController = battleController;
        this.battleEngine = battleEngine;
        this.unitInfoPopup = unitInfoPopup;
        this.exitBattle = exitBattle;
        this.actionResult = null;
        this.battleScreen=battleScreen;

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float worldY = Gdx.graphics.getHeight() - screenY;

        if (button == Input.Buttons.LEFT) {
            if (!battleInputEnabled) return true;

            Position clickedPosition = BattlefieldGeometry.screenToPosition(
                screenX,
                worldY,
                battleController.getField()
            );
            if (clickedPosition == null) return true;

            Position nearestPosition = BattlefieldGeometry.findNearestNeighbor(
                clickedPosition,
                screenX,
                worldY,
                battleEngine
            );

            setBattleInputEnabled(false);
            actionResult = battleController.onHexClicked(clickedPosition, nearestPosition);
            battleScreen.handleActionResult(actionResult);
            return true;
        }

        return false;
    }

    public void update() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            float worldY = Gdx.graphics.getHeight() - Gdx.input.getY();
            handleRightBattlefieldClick(Gdx.input.getX(), worldY);
        } else {
            unitInfoPopup.hide();
        }
    }

    public void onDefendClicked() {
        if (!battleInputEnabled) return;
        battleInputEnabled=false;
        actionResult = battleController.onDefendClicked();
        battleScreen.handleActionResult(actionResult);
    }

    public void onWaitClicked() {
        if (!battleInputEnabled) return;
        battleInputEnabled=false;
        actionResult = battleController.onWaitClicked();
        battleScreen.handleActionResult(actionResult);
    }

    public void onSpellBookClicked() {
        if (!battleInputEnabled) return;
        actionResult = battleController.onSpellBookClicked();
        battleScreen.handleActionResult(actionResult);
        //battleInputEnabled=false;
        //actionResult = battleController.
    }

    public void onExitClicked() {
        exitBattle.run();
    }

    public void setBattleInputEnabled(boolean enabled) {
        battleInputEnabled = enabled;
    }

    private void handleRightBattlefieldClick(float x, float y) {
        Position position = BattlefieldGeometry.screenToPosition(
            x,
            y,
            battleController.getField()
        );
        UnitStack unit = position == null ? null : battleController.findUnitAt(position);
        if (unit == null) {
            unitInfoPopup.hide();
            return;
        }

        unitInfoPopup.show(unit);
    }


}
