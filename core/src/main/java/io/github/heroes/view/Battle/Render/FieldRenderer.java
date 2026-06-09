package io.github.heroes.view.Battle.Render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;
import io.github.heroes.view.Battle.BattleViewConfig;
import io.github.heroes.view.Battle.BattlefieldGeometry;

public class FieldRenderer {
    private final BattleEngine battleEngine;
    private final ShapeRenderer shapeRenderer;

    public FieldRenderer(BattleEngine battleEngine) {
        this.battleEngine = battleEngine;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(boolean showHighlights) {
        drawBattlefield();
        if (showHighlights) {
            drawMovementRange();
            drawActiveUnitHighlight();
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    private void drawBattlefield() {
        BattleField field = battleEngine.getState().getField();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(1.2f);
        shapeRenderer.setColor(0.92f, 0.86f, 0.55f, 1.0f);
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Vector2 center = BattlefieldGeometry.positionToScreen(new Position(col, row));
                drawHexagon(center.x, center.y, BattleViewConfig.HEX_SIZE);
            }
        }
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f);
    }

    private void drawActiveUnitHighlight() {
        UnitStack activeUnit = battleEngine.getActiveUnit();
        if (activeUnit == null || !activeUnit.isAlive()) {
            return;
        }

        Vector2 center = BattlefieldGeometry.positionToScreen(activeUnit.getPosition());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3);
        shapeRenderer.setColor(1, 1, 0, 1);
        drawHexagon(center.x, center.y, BattleViewConfig.HEX_SIZE + 2f);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    private void drawMovementRange() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 0.45f);
        for (Position target : battleEngine.getReachablePositions()) {
            Vector2 center = BattlefieldGeometry.positionToScreen(target);
            drawFilledHexagon(center.x, center.y, BattleViewConfig.HEX_SIZE - 2f);
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
