package io.github.heroes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.MoveAndAttackAction;
import io.github.heroes.combat.MoveAction;
import io.github.heroes.model.Army;
import io.github.heroes.model.BattleField;
import io.github.heroes.model.Player;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.model.UnitType;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BattleScreen extends ScreenAdapter {
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;
    private com.badlogic.gdx.graphics.g2d.SpriteBatch batch;

    private final Main game;
    private final BattleController battleController;
    private final BattlefieldGeometry battlefieldGeometry;
    private Stage stage;
    private Skin skin;
    private Table queueTable;

    private Map<UnitType, Texture> unitTextures;

    public BattleScreen(Main game, BattleController battleController) {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        this.game = game;
        this.battleController = battleController;
        this.battlefieldGeometry = new BattlefieldGeometry();
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
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Vector2 center = battlefieldGeometry.positionToScreen(new Position(col, row));
                drawHexagon(center.x, center.y, BattleViewConfig.HEX_SIZE);
            }
        }
        shapeRenderer.end();
        drawUnits();

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
        shapeRenderer.dispose();
        batch.dispose();
        for (Texture tex : unitTextures.values()) {
            tex.dispose();
        }
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
        Vector2 center = battlefieldGeometry.positionToScreen(unit.getPosition());
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
            updateQueueUI();
        }
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
        updateQueueUI();
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
        int distance = findDistance(unit.getPosition(), targetPosition);
        return distance >= 0 && distance <= unit.getType().speed;
    }

    private int findDistance(Position start, Position target) {
        if (start.equals(target)) {
            return 0;
        }

        BattleField field = battleController.getState().getField();
        Queue<Position> queue = new ArrayDeque<>();
        Map<Position, Integer> distances = new HashMap<>();
        Set<Position> visited = new HashSet<>();

        queue.add(start);
        distances.put(start, 0);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.remove();
            int currentDistance = distances.get(current);

            for (Position neighbor : battlefieldGeometry.getNeighbors(current)) {
                if (!field.isInside(neighbor) || visited.contains(neighbor)) {
                    continue;
                }
                if (isOccupied(neighbor) && !neighbor.equals(target)) {
                    continue;
                }

                int nextDistance = currentDistance + 1;
                if (neighbor.equals(target)) {
                    return nextDistance;
                }

                queue.add(neighbor);
                distances.put(neighbor, nextDistance);
                visited.add(neighbor);
            }
        }

        return -1;
    }

    private void drawHexagon(float centerX, float centerY, float size) {
        float[] vertices = new float[12];

        for (int i = 0; i < 6; i++) {
            double angle_rad = Math.PI / 180 * (60 * i - 30);
            vertices[i * 2] = centerX + size * (float)Math.cos(angle_rad);
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
