package io.github.heroes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.BattlePathFinder;
import io.github.heroes.model.Army;
import io.github.heroes.model.BattleField;
import io.github.heroes.model.BattleState;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.model.UnitType;

import java.util.HashMap;
import java.util.Map;

public class BattleRenderer {
    private final BattleController battleController;
    private final BattlefieldGeometry battlefieldGeometry;
    private final BattlePathFinder battlePathFinder;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final Map<UnitType, Texture> unitTextures;

    public BattleRenderer(
        BattleController battleController,
        BattlefieldGeometry battlefieldGeometry,
        BattlePathFinder battlePathFinder
    ) {
        this.battleController = battleController;
        this.battlefieldGeometry = battlefieldGeometry;
        this.battlePathFinder = battlePathFinder;
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.unitTextures = new HashMap<>();

        loadTextures();
    }

    public void render() {
        drawBattlefield();
        drawMovementRange();
        drawActiveUnitHighlight();
        drawUnits();
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        for (Texture texture : unitTextures.values()) {
            texture.dispose();
        }
    }

    private void loadTextures() {
        unitTextures.put(UnitType.PIKEMAN, new Texture("pikeman.png"));
        unitTextures.put(UnitType.ARCHER, new Texture("archer.png"));
        unitTextures.put(UnitType.GRIFFIN, new Texture("griffin.png"));
    }

    private void drawBattlefield() {
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
        Texture texture = unitTextures.get(unit.getType());

        if (texture != null) {
            float size = BattleViewConfig.HEX_SIZE * BattleViewConfig.UNIT_SPRITE_SCALE;
            batch.draw(texture, center.x - size / 2f, center.y - size / 2f, size, size);
        }
    }

    private void drawActiveUnitHighlight() {
        UnitStack activeUnit = battleController.getActiveUnit();
        if (activeUnit == null || !activeUnit.isAlive()) {
            return;
        }

        Vector2 center = battlefieldGeometry.positionToScreen(activeUnit.getPosition());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(4);
        shapeRenderer.setColor(1, 1, 0, 1);
        drawHexagon(center.x, center.y, BattleViewConfig.HEX_SIZE + 2f);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    private void drawMovementRange() {
        UnitStack activeUnit = battleController.getActiveUnit();
        if (activeUnit == null || !activeUnit.isAlive()) {
            return;
        }

        BattleState state = battleController.getState();
        BattleField field = state.getField();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.5f, 0.2f, 0.5f);
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Position target = new Position(col, row);

                if (target.equals(activeUnit.getPosition())) {
                    continue;
                }

                if (battlePathFinder.canReach(state, activeUnit, target)) {
                    Vector2 center = battlefieldGeometry.positionToScreen(target);
                    drawFilledHexagon(center.x, center.y, BattleViewConfig.HEX_SIZE - 2f);
                }
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawHexagon(float centerX, float centerY, float size) {
        float[] vertices = new float[12];

        for (int i = 0; i < 6; i++) {
            double angleRad = Math.PI / 180 * (60 * i - 30);
            vertices[i * 2] = centerX + size * (float) Math.cos(angleRad);
            vertices[i * 2 + 1] = centerY + size * (float) Math.sin(angleRad);
        }
        shapeRenderer.polygon(vertices);
    }

    private void drawFilledHexagon(float centerX, float centerY, float size) {
        float[] x = new float[6];
        float[] y = new float[6];

        for (int i = 0; i < 6; i++) {
            double angleRad = Math.PI / 180 * (60 * i - 30);
            x[i] = centerX + size * (float) Math.cos(angleRad);
            y[i] = centerY + size * (float) Math.sin(angleRad);
        }

        for (int i = 0; i < 6; i++) {
            int next = (i + 1) % 6;
            shapeRenderer.triangle(centerX, centerY, x[i], y[i], x[next], y[next]);
        }
    }
}
