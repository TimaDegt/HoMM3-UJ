package io.github.heroes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.BattlePathFinder;
import io.github.heroes.model.*;
import io.github.heroes.model.anim.SpriteCoordinate;
import io.github.heroes.model.anim.Directions;

import java.util.HashMap;
import java.util.Map;

public class BattleRenderer {
    private final BattleController battleController;
    private final BattlePathFinder battlePathFinder;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;
    private final Map<UnitType, TextureRegion> unitTextures;

    private TextureRegion background;

    private void initGraphics(){
        for(UnitType type:UnitType.values()){
            String townFolder=type.getCastleType().getName();
            String unitName=type.getName();
            String unitPath="Units/"+townFolder+"/"+unitName+"_spritesheet.png";

            Texture tex=new Texture(Gdx.files.internal(unitPath));

            TextureRegion region=new TextureRegion(tex);
            unitTextures.put(type, region);
        }
    }

    public BattleRenderer(
        BattleController battleController,
        BattlePathFinder battlePathFinder
    ) {
        this.battleController = battleController;
        this.battlePathFinder = battlePathFinder;
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.glyphLayout = new GlyphLayout();
        this.unitTextures = new HashMap<>();

        loadTextures();
    }

    public void render() {
        batch.begin();
        if (background != null) {
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.end();

        drawBattlefield();
        drawMovementRange();
        drawActiveUnitHighlight();
        drawUnits();
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        for (TextureRegion region : unitTextures.values()) {
            region.getTexture().dispose();
        }
        unitTextures.clear();
        if (background != null && background.getTexture() != null) {
            background.getTexture().dispose();
        }
    }

    private void loadTextures() {
        initGraphics();

        int bgId= MathUtils.random(0,9);
        String bgPath="Battlefields/"+bgId+".png";
        Texture bgTex=new Texture(bgPath);
        this.background=new TextureRegion(bgTex);
    }

    private void drawBattlefield() {
        BattleField field = battleController.getState().getField();

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

    private void drawUnits() {
        batch.begin();
        drawArmySprites(battleController.getState().getPlayerOne().getArmy());
        drawArmySprites(battleController.getState().getPlayerTwo().getArmy());
        batch.end();

        drawUnitCountBadgeBackgrounds();
        drawUnitCountBadgeTexts();
    }

    private void drawArmySprites(Army army) {
        for (UnitStack unit : army.getUnits()) {
            //if (unit.isAlive()) {
                drawUnitSprite(unit);
            //}
        }
    }

    private void drawUnitSprite(UnitStack unit) {

        float hexHeight = BattleViewConfig.HEX_HEIGHT;
        float hexWidth = BattleViewConfig.HEX_WIDTH;

        SpriteCoordinate coordinate = unit.nextFrame();
        int x = coordinate.x;
        int y = coordinate.y;
        int squareLength = coordinate.size;
        float dx = 0;
        TextureRegion texture = new TextureRegion(unitTextures.get(unit.getType()), x, y, squareLength, squareLength); //
        Directions direction = Directions.RIGHT;
        if (unit.getOwner() == Player.PLAYER_TWO) {
            direction = Directions.LEFT;
        }
        if (unit.isMoving()) {
            direction = unit.getMovementDirection();
        }

        if (direction == Directions.LEFT) {
            texture.flip(true, false);
            dx += squareLength - hexWidth/2f;
        }

        float size = squareLength*1.5f;

        Vector2 center = BattlefieldGeometry.positionToScreen(unit.getPosition());
        batch.draw(texture, unit.deltaX + center.x - hexHeight/2f - dx, unit.deltaY + center.y - hexHeight/4f, size, size);
    }

    private void drawUnitCountBadgeBackgrounds() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.04f, 0.03f, 0.9f);
        drawArmyCountBadgeBackgrounds(battleController.getState().getPlayerOne().getArmy());
        drawArmyCountBadgeBackgrounds(battleController.getState().getPlayerTwo().getArmy());
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.85f, 0.78f, 0.45f, 1f);
        drawArmyCountBadgeBackgrounds(battleController.getState().getPlayerOne().getArmy());
        drawArmyCountBadgeBackgrounds(battleController.getState().getPlayerTwo().getArmy());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawArmyCountBadgeBackgrounds(Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive()) {
                drawUnitCountBadgeRectangle(unit);
            }
        }
    }

    private void drawUnitCountBadgeRectangle(UnitStack unit) {
        Vector2 badgePosition = getUnitCountBadgePosition(unit);
        shapeRenderer.rect(
            badgePosition.x,
            badgePosition.y,
            BattleViewConfig.UNIT_COUNT_BADGE_WIDTH,
            BattleViewConfig.UNIT_COUNT_BADGE_HEIGHT
        );
    }

    private void drawUnitCountBadgeTexts() {
        batch.begin();
        font.setColor(Color.WHITE);
        drawArmyCountBadgeTexts(battleController.getState().getPlayerOne().getArmy());
        drawArmyCountBadgeTexts(battleController.getState().getPlayerTwo().getArmy());
        batch.end();
    }

    private void drawArmyCountBadgeTexts(Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive()) {
                drawUnitCountBadgeText(unit);
            }
        }
    }

    private void drawUnitCountBadgeText(UnitStack unit) {
        String text = String.valueOf(unit.getCount());
        Vector2 badgePosition = getUnitCountBadgePosition(unit);
        glyphLayout.setText(font, text);

        float textX = badgePosition.x + (BattleViewConfig.UNIT_COUNT_BADGE_WIDTH - glyphLayout.width) / 2f;
        float textY = badgePosition.y + (BattleViewConfig.UNIT_COUNT_BADGE_HEIGHT + glyphLayout.height) / 2f;
        font.draw(batch, text, textX, textY);
    }

    private Vector2 getUnitCountBadgePosition(UnitStack unit) {
        Vector2 center = BattlefieldGeometry.positionToScreen(unit.getPosition());
        float spriteSize = unit.getType().getAnimParams().getSize();
        float hexHeight = BattleViewConfig.HEX_HEIGHT;
        float hexWidth = BattleViewConfig.HEX_WIDTH;

        float badgeX = center.x + hexWidth/4f;//BattleViewConfig.UNIT_COUNT_BADGE_WIDTH / 2f;
        float badgeY = center.y - hexHeight/4f;//spriteSize / 2f + BattleViewConfig.UNIT_COUNT_BADGE_Y_OFFSET;
        if (unit.getOwner() == Player.PLAYER_TWO) {
            badgeX-=3f*hexWidth/4f;
        }

        return new Vector2(badgeX, badgeY);
    }

    private void drawActiveUnitHighlight() {
        UnitStack activeUnit = battleController.getActiveUnit();
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
        UnitStack activeUnit = battleController.getActiveUnit();
        if (activeUnit == null || !activeUnit.isAlive()) {
            return;
        }

        BattleState state = battleController.getState();
        BattleField field = state.getField();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 0.45f);
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Position target = new Position(col, row);

                if (battleController.findUnitAt(target)!=null) {
                    continue;
                }

                if (battlePathFinder.canReach(state, activeUnit, target)) {
                    Vector2 center = BattlefieldGeometry.positionToScreen(target);
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
