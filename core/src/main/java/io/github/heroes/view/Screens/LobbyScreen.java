package io.github.heroes.view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.heroes.setup.BattleFactory1v1;
import io.github.heroes.setup.BattleFactoryEasy;
import io.github.heroes.view.Battle.BattleScreen;
import io.github.heroes.view.Main;

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
    private final Texture buttonTexture;
    private Music lobbyMusic;

    public enum GameMode {
        DEMO("Mode: Demo AI"),
        LOCAL_1V1("Mode: 1v1 Local");

        public final String label;
        GameMode(String label) { this.label = label; }
        public GameMode next() { return values()[(this.ordinal() + 1) % values().length]; }
    }

    public enum Difficulty {
        EASY("Difficulty: Easy"),
        NORMAL("Difficulty: Normal"),
        HARD("Difficulty: Hard");

        public final String label;
        Difficulty(String label) { this.label = label; }
        public Difficulty next() { return values()[(this.ordinal() + 1) % values().length]; }
    }

    private Difficulty currentDifficulty = Difficulty.NORMAL;
    private GameMode currentGameMode = GameMode.DEMO;

    public LobbyScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        background = new Texture("Menu/lobby_background.jpg");
        buttonTexture = new Texture("Menu/button.png");
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        setupMusic();
        setupUI();
    }

    private void setupMusic() {
        lobbyMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/Kobza.mp3"));
        lobbyMusic.setLooping(true);
        lobbyMusic.setVolume(0.5f);
        lobbyMusic.play();
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.right().padRight(MENU_RIGHT_PADDING);
        stage.addActor(table);

        TextButton.TextButtonStyle myButtonStyle = new TextButton.TextButtonStyle();
        myButtonStyle.font = skin.getFont("default-font");
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        myButtonStyle.up = buttonDrawable;
        myButtonStyle.down = buttonDrawable.tint(Color.LIGHT_GRAY);
        myButtonStyle.disabled = buttonDrawable.tint(Color.DARK_GRAY);

        TextButton modeButton = createMenuButton(currentGameMode.label, myButtonStyle);
        TextButton difficultyButton = createMenuButton(currentDifficulty.label, myButtonStyle);

        modeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentGameMode = currentGameMode.next();
                modeButton.setText(currentGameMode.label);

                if (currentGameMode == GameMode.LOCAL_1V1) {
                    difficultyButton.setDisabled(true);
                    difficultyButton.setText("Diff: N/A");
                } else {
                    difficultyButton.setDisabled(false);
                    difficultyButton.setText(currentDifficulty.label);
                }
            }
        });

        difficultyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (difficultyButton.isDisabled()) return;

                currentDifficulty = currentDifficulty.next();
                difficultyButton.setText(currentDifficulty.label);
            }
        });

        TextButton startButton = createMenuButton("Start Game", myButtonStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (lobbyMusic != null) {
                    lobbyMusic.stop();
                }
                if (currentGameMode == GameMode.LOCAL_1V1) {
                    game.setScreen(new BattleScreen(game, BattleFactory1v1.createDemoBattle()));
                } else {
                    game.setScreen(new BattleScreen(game, BattleFactoryEasy.createDemoBattle()));
                }
            }
        });

        TextButton exitButton = createMenuButton("Exit", myButtonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(modeButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PADDING);
        table.row();
        table.add(difficultyButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PADDING);
        table.row();
        table.add(startButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PADDING);
        table.row();
        table.add(exitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PADDING);
    }

    private TextButton createMenuButton(String text, TextButton.TextButtonStyle style) {
        TextButton button = new TextButton(text, style);
        button.getLabel().setFontScale(1.5f);
        button.padBottom(12f);
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
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
    public void show() {
        if (lobbyMusic != null && !lobbyMusic.isPlaying()) {
            lobbyMusic.play();
        }
    }

    @Override
    public void dispose() {
        if (lobbyMusic != null) {
            lobbyMusic.stop();
            lobbyMusic.dispose();
        }
        stage.dispose();
        skin.dispose();
        batch.dispose();
        background.dispose();
        buttonTexture.dispose();
    }
}
