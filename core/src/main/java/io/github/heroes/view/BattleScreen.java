package io.github.heroes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.heroes.combat.BattlePathFinder;
import io.github.heroes.combat.BattleController;
import io.github.heroes.control.BattleScreenController;
import io.github.heroes.model.*;
import io.github.heroes.view.ui.BattleActionPanel;
import io.github.heroes.view.ui.UnitInfoPopup;

public class BattleScreen extends ScreenAdapter {
    private final Main game;
    private final BattleController battleController;
    private final BattlePathFinder battlePathFinder;
    private final BattleRenderer battleRenderer;
    private final BattleScreenController battleScreenController;
    private final Stage stage;
    private Stage actionPanelStage;
    private final Skin skin;
    private BattleActionPanel actionPanel;
    private UnitInfoPopup unitInfoPopup;

    public BattleScreen(Main game, BattleController battleController) {
        this.game = game;
        this.battleController = battleController;
        this.battlePathFinder = new BattlePathFinder();
        this.battleRenderer = new BattleRenderer(battleController, battlePathFinder);
        this.battleScreenController = new BattleScreenController(
            battleController,
            battlePathFinder
        );
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

        actionPanel = new BattleActionPanel(battleController);
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
                    if (battleScreenController.handleLeftBattlefieldClick(screenX, worldY)) {
                        finishTurn();
                    }
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


    private void handleRightBattlefieldClick(float x, float y) {
        UnitStack unit = battleScreenController.findUnitUnderCursor(x, y);
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

    private void finishTurn() {
        if (battleController.getState().isFinished()) {
            game.setScreen(new VictoryScreen(game));
            return;
        }

        actionPanel.updateQueueButtons();
    }
}
