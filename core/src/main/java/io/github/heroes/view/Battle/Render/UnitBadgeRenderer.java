package io.github.heroes.view.Battle.Render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.control.BattleController;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.Player;
import io.github.heroes.view.Battle.BattleViewConfig;
import io.github.heroes.view.Battle.BattlefieldGeometry;
import io.github.heroes.view.Battle.animation.BattleAnimationPlayer;
import io.github.heroes.view.Battle.animation.FrameData;

import java.util.List;

public class UnitBadgeRenderer {
    private final BattleController battleController;
    private final BattleAnimationPlayer animationPlayer;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;

    public UnitBadgeRenderer(
        BattleController battleController,
        BattleAnimationPlayer animationPlayer
    ) {
        this.battleController = battleController;
        this.animationPlayer = animationPlayer;
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.glyphLayout = new GlyphLayout();
    }

    public void render() {
        List<UnitSnapshot> units = battleController.getBattleSnapshot().units();
        drawUnitCountBadgeBackgrounds(units);
        drawUnitCountBadgeTexts(units);
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }

    private void drawUnitCountBadgeBackgrounds(List<UnitSnapshot> units) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.04f, 0.03f, 0.9f);
        drawBadgeBackgrounds(units);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.85f, 0.78f, 0.45f, 1f);
        drawBadgeBackgrounds(units);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawBadgeBackgrounds(List<UnitSnapshot> units) {
        for (UnitSnapshot unit : units) {
            if (unit.alive()) drawUnitCountBadgeRectangle(unit);
        }
    }

    private void drawUnitCountBadgeRectangle(UnitSnapshot unit) {
        Vector2 badgePosition = getUnitCountBadgePosition(unit);
        shapeRenderer.rect(
            badgePosition.x,
            badgePosition.y,
            BattleViewConfig.UNIT_COUNT_BADGE_WIDTH,
            BattleViewConfig.UNIT_COUNT_BADGE_HEIGHT
        );
    }

    private void drawUnitCountBadgeTexts(List<UnitSnapshot> units) {
        batch.begin();
        font.setColor(Color.WHITE);
        for (UnitSnapshot unit : units) {
            if (unit.alive()) drawUnitCountBadgeText(unit);
        }
        batch.end();
    }

    private void drawUnitCountBadgeText(UnitSnapshot unit) {
        String text = String.valueOf(unit.count());
        Vector2 badgePosition = getUnitCountBadgePosition(unit);
        glyphLayout.setText(font, text);

        float textX = badgePosition.x + (BattleViewConfig.UNIT_COUNT_BADGE_WIDTH - glyphLayout.width) / 2f;
        float textY = badgePosition.y + (BattleViewConfig.UNIT_COUNT_BADGE_HEIGHT + glyphLayout.height) / 2f;
        font.draw(batch, text, textX, textY);
    }

    private Vector2 getUnitCountBadgePosition(UnitSnapshot unit) {
        Vector2 center = BattlefieldGeometry.positionToScreen(unit.position());

        float dx = 0;
        float dy = 0;
        FrameData frameData = animationPlayer.getAnimationEngine(unit).nextFrame();
        if (frameData.isMoving()) {
            dx += frameData.getDx();
            dy += frameData.getDy();
        } else {
            dx += center.x;
            dy += center.y;
        }

        float badgeX = dx;
        float badgeY = dy - 14 * BattleViewConfig.HEX_HEIGHT / 32f;
        if (unit.owner() == Player.PLAYER_TWO) {
            badgeX -= BattleViewConfig.UNIT_COUNT_BADGE_WIDTH;
        }

        return new Vector2(badgeX, badgeY);
    }
}
