package io.github.heroes.view.Battle;

import com.badlogic.gdx.Gdx;
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
import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.control.BattleController;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.view.Main;
import io.github.heroes.view.Battle.InputHandler.BattleInputHandler;
import io.github.heroes.view.Screens.LobbyScreen;
import io.github.heroes.view.Screens.VictoryScreen;

public class BattleScreen extends ScreenAdapter {
    private final Main game;
    private final BattleEngine battleEngine;
    private final BattleRenderer battleRenderer;
    private final Stage stage;
    private final Skin skin;
    private final UnitInfoPopup unitInfoPopup;
    private final BattleController battleController;
    private final BattleInputHandler battleInputHandler;
    private BattleActionPanel actionPanel;

    public BattleScreen(Main game, BattleEngine battleEngine) {
        this.game = game;
        this.battleEngine = battleEngine;
        this.battleRenderer = new BattleRenderer(battleEngine);
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        unitInfoPopup = new UnitInfoPopup(skin);
        battleController = new BattleController(battleEngine);
        battleInputHandler = new BattleInputHandler(
            battleController,
            battleEngine,
            unitInfoPopup,
            this,
            () -> game.setScreen(new LobbyScreen(game))
        );

        setupUI();
        setupInput();
        requestNextAction();
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.top().right();
        stage.addActor(table);


        actionPanel = new BattleActionPanel(
            battleEngine.getTurnQueueOrder(),
            battleInputHandler::onDefendClicked,
            battleInputHandler::onWaitClicked,
            battleInputHandler::onSpellBookClicked,
            () -> game.setScreen(new LobbyScreen(game)),
            () -> game.setScreen(new LobbyScreen(game))
        );
        actionPanel.addTo(stage);

        unitInfoPopup.addTo(stage);
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(battleInputHandler);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.4f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        battleInputHandler.update();

        battleRenderer.render(delta);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        BattleField field = battleEngine.getState().getField();
        BattleViewConfig.updateDimensions(width, height, field.getWidth(), field.getHeight());
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        battleRenderer.dispose();
    }

    public void setBattleInputEnabled(boolean enabled) {
        battleInputHandler.setBattleInputEnabled(enabled);
        actionPanel.setBattleInputEnabled(enabled);
    }

    public void handleActionResult(ActionResult result) {
        if (!result.successful()) {
            setBattleInputEnabled(true);
            return;
        }
        setBattleInputEnabled(false);
        battleRenderer.playActionAnimation(result, this::onActionAnimationFinished);
    }

    private void onActionAnimationFinished() {
        actionPanel.updateQueueButtons(battleEngine.getTurnQueueOrder());

        if (battleEngine.getState().isFinished()) {
            game.setScreen(new VictoryScreen(game, battleEngine.getState().getWinner()));
            return;
        }

        requestNextAction();
    }

    private void requestNextAction() {
        ActionResult nextResult = battleController.onReadyForNextAction();
        if (nextResult.successful()) {
            handleActionResult(nextResult);
            return;
        }

        setBattleInputEnabled(true);
    }
}
