package io.github.heroes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.heroes.combat.BattlePathFinder;
import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.MoveAndAttackAction;
import io.github.heroes.combat.MoveAction;
import io.github.heroes.model.*;
import io.github.heroes.view.ui.BattleActionPanel;
import io.github.heroes.view.ui.UnitInfoPopup;

public class BattleScreen extends ScreenAdapter {
    private final Main game;
    private final BattleController battleController;
    private final BattlefieldGeometry battlefieldGeometry;
    private final BattlePathFinder battlePathFinder;
    private final BattleRenderer battleRenderer;
    private final Stage stage;
    private final Skin skin;
    private BattleActionPanel actionPanel;
    private UnitInfoPopup unitInfoPopup;

    public BattleScreen(Main game, BattleController battleController) {
        this.game = game;
        this.battleController = battleController;
        this.battlefieldGeometry = new BattlefieldGeometry();
        this.battlePathFinder = new BattlePathFinder(battlefieldGeometry);
        this.battleRenderer = new BattleRenderer(battleController, battlefieldGeometry, battlePathFinder);
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        setupUI();
        setupInput();
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.top().right();
        stage.addActor(table);

        TextButton backButton = new TextButton("Back to lobby", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LobbyScreen(game));
            }
        });
        table.add(backButton).pad(20);

        actionPanel = new BattleActionPanel(skin, battleController);
        actionPanel.addTo(stage);

        setupUnitInfoPopup();
    }

    private void setupUnitInfoPopup() {
        unitInfoPopup = new UnitInfoPopup(skin);
        unitInfoPopup.addTo(stage);
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float worldY = Gdx.graphics.getHeight() - screenY;

                if (button == Input.Buttons.LEFT) {
                    handleLeftBattlefieldClick(screenX, worldY);
                    return true;
                }

                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.4f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            float worldY = Gdx.graphics.getHeight() - Gdx.input.getY();
            handleRightBattlefieldClick(Gdx.input.getX(), worldY);
        } else {
            hideUnitInfoPopup();
        }

        battleRenderer.render();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        BattleField field = battleController.getState().getField();
        BattleViewConfig.updateDimensions(width, height, field.getWidth(), field.getHeight());
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        battleRenderer.dispose();
    }


    private void handleLeftBattlefieldClick(float x, float y) {
        if (battleController.getState().isFinished()) {
            return;
        }

        Position clickedPosition = battlefieldGeometry.screenToPosition(
            x,
            y,
            battleController.getState().getField()
        );
        if (clickedPosition == null) {
            return;
        }

        UnitStack clickedUnit = findUnitAt(clickedPosition);
        if (clickedUnit != null) {
            handleUnitClick(clickedUnit, x, y);
            return;
        }

        UnitStack activeUnit = battleController.getActiveUnit();
        if (canReach(activeUnit, clickedPosition)) {
            battleController.performAction(new MoveAction(activeUnit, clickedPosition));
            finishTurn();
        }
    }

    private void handleRightBattlefieldClick(float x, float y) {
        Position clickedPosition = battlefieldGeometry.screenToPosition(
            x,
            y,
            battleController.getState().getField()
        );
        if (clickedPosition == null) {
            hideUnitInfoPopup();
            return;
        }

        UnitStack unit = findUnitAt(clickedPosition);
        if (unit == null) {
            hideUnitInfoPopup();
            return;
        }

        showUnitInfoPopup(unit);
    }

    private void showUnitInfoPopup(UnitStack unit) {
        unitInfoPopup.show(unit);
    }

    private void hideUnitInfoPopup() {
        unitInfoPopup.hide();
    }

    private void handleUnitClick(UnitStack clickedUnit, float x, float y) {
        UnitStack activeUnit = battleController.getActiveUnit();

        if (clickedUnit.getOwner() == activeUnit.getOwner()) {
            return;
        }

        Position attackPosition = findNearestAttackPosition(clickedUnit.getPosition(), x, y);
        if (attackPosition == null || !canReach(activeUnit, attackPosition)) {
            return;
        }

        battleController.performAction(new MoveAndAttackAction(activeUnit, attackPosition, clickedUnit));
        finishTurn();
    }

    private void finishTurn() {
        if (battleController.getState().isFinished()) {
            game.setScreen(new VictoryScreen(game));
            return;
        }

        actionPanel.updateQueueButtons();
    }

    private boolean isOccupied(Position position) {
        return isOccupiedByArmy(position, battleController.getState().getPlayerOne().getArmy())
            || isOccupiedByArmy(position, battleController.getState().getPlayerTwo().getArmy());
    }

    private UnitStack findUnitAt(Position position) {
        UnitStack unit = findUnitAtArmy(position, battleController.getState().getPlayerOne().getArmy());
        if (unit != null) {
            return unit;
        }

        return findUnitAtArmy(position, battleController.getState().getPlayerTwo().getArmy());
    }

    private UnitStack findUnitAtArmy(Position position, Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive() && unit.getPosition().equals(position)) {
                return unit;
            }
        }

        return null;
    }

    private boolean isOccupiedByArmy(Position position, Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive() && unit.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
    }

    private Position findNearestAttackPosition(Position targetPosition, float clickX, float clickY) {
        Position nearestPosition = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Position neighbor : battlefieldGeometry.getNeighbors(targetPosition)) {
            if (!battleController.getState().getField().isInside(neighbor)) {
                continue;
            }
            if (isOccupied(neighbor) && !neighbor.equals(battleController.getActiveUnit().getPosition())) {
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
