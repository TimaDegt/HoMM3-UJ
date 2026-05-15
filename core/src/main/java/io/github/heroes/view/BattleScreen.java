package io.github.heroes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.MoveAction;
import io.github.heroes.model.Army;
import io.github.heroes.model.BattleField;
import io.github.heroes.model.Player;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.model.UnitType;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import java.util.List;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;

public class BattleScreen extends ScreenAdapter {
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;
    private com.badlogic.gdx.graphics.g2d.SpriteBatch batch;

    private final Main game;
    private final BattleController battleController;
    private Stage stage;
    private Skin skin;
    private Table queueTable;

    private Map<UnitType, Texture> unitTextures;

    public BattleScreen(Main game, BattleController battleController) {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        this.game = game;
        this.battleController = battleController;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        loadTextures();
        setupUI();
        setupInput();
        updateQueueUI();
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
        queueTable = new Table();
        queueTable.setFillParent(true);
        queueTable.bottom();
        stage.addActor(queueTable);
    }


    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(stage);

        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float worldY = Gdx.graphics.getHeight() - screenY;
                handleBattlefieldClick(screenX, worldY);
                return true;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
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

    private void handleBattlefieldClick(float x, float y) {
        if (battleController.getState().isFinished()) {
            return;
        }

        Position clickedPosition = screenToPosition(x, y);
        if (clickedPosition == null) {
            System.out.println("LOG: Клік повз гекси");
            return;
        }

        if (isOccupied(clickedPosition)) {
            System.out.println("LOG: Гекс зайнятий іншим юнітом!");
            return;
        }

        System.out.println("LOG: Спроба ходу на X:" + clickedPosition.x() + " Y:" + clickedPosition.y());

        try {
            // Робимо хід
            battleController.performAction(new MoveAction(battleController.getActiveUnit(), clickedPosition));
            System.out.println("LOG: Хід успішний!");

            updateQueueUI();

        } catch (Exception e) {
            System.out.println("LOG: Помилка під час ходу: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Position screenToPosition(float screenX, float screenY) {
        BattleField field = battleController.getState().getField();
        Position closestPosition = null;
        float closestDistance = Float.MAX_VALUE;

        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Position position = new Position(col, row);
                Vector2 center = positionToScreen(position);
                float distance = center.dst(screenX, screenY);

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPosition = position;
                }
            }
        }

        if (closestDistance <= BattleViewConfig.HEX_SIZE) {
            return closestPosition;
        }

        return null;
    }

    private boolean isOccupied(Position position) {
        return isOccupiedByArmy(position, battleController.getState().getPlayerOne().getArmy())
            || isOccupiedByArmy(position, battleController.getState().getPlayerTwo().getArmy());
    }

    private boolean isOccupiedByArmy(Position position, Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive() && unit.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
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


    public void updateQueueUI() {
        if (queueTable == null) return;

        queueTable.clear();
        queueTable.padBottom(20);
        List<UnitStack> queue = battleController.getTurnQueueOrder();
        UnitStack activeUnit = battleController.getActiveUnit();

        if (queue == null || queue.isEmpty()) return;

        boolean startDrawing = false;

        for (UnitStack unit : queue) {
            if (!unit.isAlive()) continue;

            if (unit == activeUnit) {
                startDrawing = true;
            }

            if (startDrawing) {
                String text = unit.getType().name() + " (" + unit.getCount() + ")";
                Label unitLabel = new Label(text, skin);

                if (unit == activeUnit) {
                    unitLabel.setText("=> " + text + " <=");
                    unitLabel.setFontScale(1.2f);
                }
                if (unit.getOwner() == Player.PLAYER_ONE) {
                    unitLabel.setColor(0.5f, 0.7f, 1f, 1f);
                } else {
                    unitLabel.setColor(1f, 0.5f, 0.5f, 1f);
                }

                queueTable.add(unitLabel).padLeft(15).padRight(15);
            }
        }
    }
}
