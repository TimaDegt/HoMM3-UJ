package io.github.heroes.view.Battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.control.BattleController;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.view.Battle.Render.BattleRenderer;
import io.github.heroes.view.Main;
import io.github.heroes.view.Battle.InputHandler.BattleInputHandler;
import io.github.heroes.view.Battle.animation.BattleAnimationPlayer;
import io.github.heroes.view.Screens.LobbyScreen;
import io.github.heroes.view.Screens.VictoryScreen;

public class BattleScreen extends ScreenAdapter {
    private final Main game;
    private final BattleEngine battleEngine;
    private final BattleAnimationPlayer animationPlayer;
    private final BattleRenderer battleRenderer;
    private final Stage stage;
    private final Skin skin;
    private final UnitInfoPopup unitInfoPopup;
    private final BattleController battleController;
    private final BattleInputHandler battleInputHandler;
    private BattleActionPanel actionPanel;
    private Music battleMusic;

    public BattleScreen(Main game, BattleEngine battleEngine) {
        this.game = game;
        this.battleEngine = battleEngine;
        this.animationPlayer = new BattleAnimationPlayer();
        this.battleRenderer = new BattleRenderer(battleEngine, animationPlayer);
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        unitInfoPopup = new UnitInfoPopup(skin);
        battleController = new BattleController(battleEngine);
        battleInputHandler = new BattleInputHandler(
            battleController,
            battleEngine,
            unitInfoPopup,
            this,
            this::exitToLobby
        );

        setupUI();
        setupInput();
        setupMusic();
        requestNextAction();
    }

    private void exitToLobby() {
        if (battleMusic != null) {
            battleMusic.stop();
        }
        game.setScreen(new LobbyScreen(game));
    }

    private void setupMusic() {
        battleMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/secret.mp3"));
        battleMusic.setLooping(true);
        battleMusic.setVolume(0.5f);
        battleMusic.play();
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
            this::exitToLobby,
            this::exitToLobby
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
        if (battleMusic != null) {
            battleMusic.stop();
            battleMusic.dispose();
        }
        stage.dispose();
        skin.dispose();
        battleRenderer.dispose();
        animationPlayer.clear();
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
            if (battleMusic != null) {
                battleMusic.stop();
            }
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
