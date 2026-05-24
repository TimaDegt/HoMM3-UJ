package io.github.heroes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.heroes.setup.BattleFactory;

public class LobbyScreen extends ScreenAdapter {
    private static final float BUTTON_WIDTH = 240f;
    private static final float BUTTON_HEIGHT = 64f;
    private static final float MENU_RIGHT_PADDING = 64f;
    private static final float BUTTON_PADDING = 12f;

    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final Texture background;

    public LobbyScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        background = new Texture("Menu/lobby_background.jpg");
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        setupUI();
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.right().center().padRight(MENU_RIGHT_PADDING);
        stage.addActor(table);

        TextButton startButton = new TextButton("Start Game", skin, "blue");
        TextButton exitButton = new TextButton("Exit", skin, "blue");
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new BattleScreen(game, BattleFactory.createDemoBattle()));
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });
        table.add(startButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PADDING);
        table.row();
        table.add(exitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PADDING);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        background.dispose();
    }
}
