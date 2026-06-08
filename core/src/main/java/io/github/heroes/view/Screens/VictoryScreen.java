package io.github.heroes.view.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.heroes.model.state.Player;
import io.github.heroes.view.Main;

public class VictoryScreen extends ScreenAdapter {
    private final Main game;
    private final Player winner;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final Texture background;
    private final Texture buttonTexture;

    public VictoryScreen(Main game, Player winner) {
        this.game = game;
        this.winner = winner;

        batch = new SpriteBatch();
        background = new Texture("Menu/endgamebg.png");
        buttonTexture = new Texture("Menu/button.png");

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        setupUI();
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        String winText = "DRAW!";
        Color winColor = Color.GOLD;

        if (winner == Player.PLAYER_ONE) {
            winText = "P1 WON!";
            winColor = new Color(0.5f, 0.7f, 1f, 1f);
        } else if (winner == Player.PLAYER_TWO) {
            winText = "P2 WON!";
            winColor = new Color(1f, 0.5f, 0.5f, 1f);
        }

        Label title = new Label(winText, skin);
        title.setFontScale(3.5f);
        title.setColor(winColor);

        TextButton.TextButtonStyle myButtonStyle = new TextButton.TextButtonStyle();
        myButtonStyle.font = skin.getFont("default-font");
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        myButtonStyle.up = buttonDrawable;
        myButtonStyle.down = buttonDrawable.tint(Color.LIGHT_GRAY);

        TextButton lobbyButton = createMenuButton("Exit to lobby", myButtonStyle);
        lobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LobbyScreen(game));
            }
        });

        table.add(title).padBottom(60f);
        table.row();
        table.add(lobbyButton).width(240f).height(64f);
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
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.1f, 1);
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
        buttonTexture.dispose();
    }
}
