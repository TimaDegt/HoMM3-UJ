package io.github.heroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

import io.github.heroes.combat.BattleController;
import io.github.heroes.model.Army;
import io.github.heroes.model.BattleField;
import io.github.heroes.model.Player;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.model.UnitType;
import io.github.heroes.view.BattleViewConfig;

public class BattleScreen extends ScreenAdapter {
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;
    private com.badlogic.gdx.graphics.g2d.SpriteBatch batch;

    private final Main game;
    private final BattleController battleController;
    private Stage stage;
    private Skin skin;

    private Map<UnitType, Texture> unitTextures;

    public BattleScreen(Main game, BattleController battleController) {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        this.game = game;
        this.battleController = battleController;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        loadTextures();
        setupUI();
    }

    private void loadTextures() {
        unitTextures = new HashMap<>();
        unitTextures.put(UnitType.PIKEMAN, new Texture("pikeman.png"));
        unitTextures.put(UnitType.ARCHER, new Texture("archer.png"));
        unitTextures.put(UnitType.GRIFFIN, new Texture("griffin.png"));
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
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.4f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        BattleField field = battleController.getState().getField();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1);
        for(int row=0; row<field.getHeight(); row++){
            for(int col=0; col<field.getWidth(); col++){
                Vector2 center = positionToScreen(new Position(col, row));
                drawHexagon(center.x, center.y, BattleViewConfig.HEX_SIZE);
            }
        }
        shapeRenderer.end();
        drawUnits();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }

    public void dispose(){
        stage.dispose();
        skin.dispose();
        shapeRenderer.dispose();
        batch.dispose();
        for (Texture tex : unitTextures.values()) {
            tex.dispose();
        }
    }

    private Vector2 positionToScreen(Position position) {
        float x = BattleViewConfig.FIELD_START_X + position.x() * BattleViewConfig.HEX_WIDTH;
        float y = BattleViewConfig.FIELD_START_Y + position.y() * BattleViewConfig.HEX_HEIGHT * 0.75f;

        if (position.y() % 2 == 1) {
            x += BattleViewConfig.HEX_WIDTH / 2;
        }

        return new Vector2(x, y);
    }

    private void drawUnits() {
        batch.begin();
        drawArmy(battleController.getState().getPlayerOne().getArmy());
        drawArmy(battleController.getState().getPlayerTwo().getArmy());
        batch.end();
    }

    private void drawArmy(Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive()) {
                drawUnit(unit);
            }
        }
    }

    private void drawUnit(UnitStack unit) {
        Vector2 center = positionToScreen(unit.getPosition());
        Texture tex = unitTextures.get(unit.getType());

        if (tex != null) {
            float size = BattleViewConfig.HEX_SIZE * 1.8f;
            batch.draw(tex, center.x - size / 2f, center.y - size / 2f, size, size);
        }
    }

    private void drawHexagon(float centerX, float centerY, float size) {
        float[] vertices = new float[12];

        for (int i = 0; i < 6; i++) {
            double angle_rad = Math.PI/180 * (60*i-30);
            vertices[i * 2] = centerX+size * (float)Math.cos(angle_rad);
            vertices[i * 2 + 1] = centerY + size * (float)Math.sin(angle_rad);
        }
        shapeRenderer.polygon(vertices);
    }
}
