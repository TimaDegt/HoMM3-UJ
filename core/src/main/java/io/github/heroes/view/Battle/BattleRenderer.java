package io.github.heroes.view.Battle;

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
import io.github.heroes.anim.Animation;
import io.github.heroes.anim.FrameData;
import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.combat.BattlePathFinder;
import io.github.heroes.combat.cursor.Cursor;
import io.github.heroes.combat.cursor.CursorType;
import io.github.heroes.model.state.*;

import java.util.*;

import static io.github.heroes.view.Battle.BattleViewConfig.CURSOR_SCALE;

public class BattleRenderer {
    private static final float ACTION_ANIMATION_DURATION = 1f;

    private final BattleEngine battleEngine;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout;
    private final Map<UnitType, TextureRegion> unitTextures;
    private final Map<CursorType, TextureRegion> cursorTextures;

    private TextureRegion background;
    private Runnable actionAnimationFinished;

    private void initGraphics(){
        for(UnitType type:UnitType.values()){
            String townFolder=type.getCastleType().getName();
            String unitName=type.getName();
            String unitPath="Units/"+townFolder+"/"+unitName+"_spritesheet.png";

            Texture tex=new Texture(Gdx.files.internal(unitPath));
            TextureRegion region = new TextureRegion(tex);//createStaticUnitRegion(type, tex);
            unitTextures.put(type, region);
        }
        for (CursorType cursorType:CursorType.values()){
            if (cursorType == CursorType.NONE) continue;
            String path = "Icons/Cursors/"+cursorType.getName()+".png";
            Texture tex=new Texture(Gdx.files.internal(path));
            TextureRegion region=new TextureRegion(tex);
            cursorTextures.put(cursorType, region);
        }
    }

    private TextureRegion createStaticUnitRegion(UnitType type, Texture texture) {
        return switch (type) {
            case PIKEMAN -> new TextureRegion(texture, 0, 7 * 125, 125, 125);
            case ARCHER -> new TextureRegion(texture, 0, 6 * 125, 125, 125);
            case GRIFFIN -> new TextureRegion(texture, 0, 0, 155, 155);
        };
    }

    public BattleRenderer(
        BattleEngine battleEngine
    ) {
        this.battleEngine = battleEngine;
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.glyphLayout = new GlyphLayout();
        this.unitTextures = new HashMap<>();
        this.cursorTextures = new HashMap<>();

        loadTextures();
    }

    private void drawCustomCursor() {
        CursorType cursorType = Cursor.getCustomCursor(battleEngine);
        if (cursorType == CursorType.NONE) {
            Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
            return;
        }
        Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.None);
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        TextureRegion regionToDraw = cursorTextures.get(cursorType);

        if (regionToDraw != null) {
            float scaledWidth = regionToDraw.getRegionWidth() * CURSOR_SCALE;
            float scaledHeight = regionToDraw.getRegionHeight() * CURSOR_SCALE;
            batch.begin();
            batch.draw(regionToDraw, mouseX + cursorType.offX * CURSOR_SCALE, mouseY - scaledHeight + cursorType.offY * CURSOR_SCALE, scaledWidth, scaledHeight);
            batch.end();
        }
    }

    public void render(float delta) {
        batch.begin();
        if (background != null) {
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.end();

        drawBattlefield();
        boolean noAnimations = allAnimationsFinished();
        if (noAnimations) {
            drawMovementRange();
            drawActiveUnitHighlight();
        }
        drawUnits();
        if (noAnimations) {
            drawCustomCursor();
        }
        updateActionAnimation(delta);
    }

    //part for timur
    private List<BattleEvent> events;
    public void playActionAnimation(ActionResult result, Runnable onFinished) {
        if (result == null) throw new IllegalArgumentException("Action result cannot be null");
        if (onFinished == null) throw new IllegalArgumentException("Completion callback cannot be null");

        this.events = new ArrayList<>
            (result.events());

        while (!this.events.isEmpty()) {
            BattleEvent event = this.events.get(0);

            if (event instanceof BattleEvent.UnitMoved move) {
                UnitStack unit = move.unit();
                //unit.getAnimationEngine().startMovement(move.path());
            } else if (event instanceof BattleEvent.UnitAttacked attack) {
                UnitStack unit = attack.attacker();
                //unit.getAnimationEngine().startAttack();
            } else if (event instanceof BattleEvent.UnitDamaged receive) {
                UnitStack unit = receive.unit();
                //unit.getAnimationEngine().startReceiveDamage();
            } else if (event instanceof BattleEvent.UnitDied death) {
                UnitStack unit = death.unit();
                //unit.getAnimationEngine().startDeath();
            } else {
                this.events.remove(0);
                continue;
            }
            break;
        }
        actionAnimationFinished = onFinished;
    }

    private boolean allAnimationsFinished() {
        boolean allAnimationsFinished = true;
        for (UnitStack unit : battleEngine.getState().getPlayerOne().getArmy().getUnits()) {
           // allAnimationsFinished &= unit.getAnimationEngine().finished();
        }
        for (UnitStack unit : battleEngine.getState().getPlayerTwo().getArmy().getUnits()) {
           // allAnimationsFinished &= unit.getAnimationEngine().finished();
        }
        return allAnimationsFinished;
    }
    private void updateActionAnimation(float delta) {
        if (actionAnimationFinished == null) return;

        if (!allAnimationsFinished()) {
            for (UnitStack unit : battleEngine.getState().getPlayerOne().getArmy().getUnits()) {
               // unit.getAnimationEngine().updatik(delta);
            }
            for (UnitStack unit : battleEngine.getState().getPlayerTwo().getArmy().getUnits()) {
                //unit.getAnimationEngine().updatik(delta);
            }
            return;
        }
        if (!this.events.isEmpty()) {
            this.events.remove(0);
        }
        while (!this.events.isEmpty()) {
            BattleEvent event = this.events.get(0);

            if (event instanceof BattleEvent.UnitMoved move) {
                UnitStack unit = move.unit();
            //    unit.getAnimationEngine().startMovement(move.path());
            } else if (event instanceof BattleEvent.UnitAttacked attack) {
                UnitStack unit = attack.attacker();
           //     unit.getAnimationEngine().startAttack();
            } else if (event instanceof BattleEvent.UnitDamaged receive) {
                UnitStack unit = receive.unit();
           //     unit.getAnimationEngine().startReceiveDamage();
            } else if (event instanceof BattleEvent.UnitDied death) {
                UnitStack unit = death.unit();
            //    unit.getAnimationEngine().startDeath();
            } else {
                events.remove(0);
                continue;
            }
            break;
        }
        if (!this.events.isEmpty()) return;

        Runnable onFinished = actionAnimationFinished;
        actionAnimationFinished = null;
        onFinished.run();
    }
//
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
        for (TextureRegion region : cursorTextures.values()) {
            region.getTexture().dispose();
        }
        cursorTextures.clear();
    }

    private void loadTextures() {
        initGraphics();

        int bgId= MathUtils.random(0,9);
        String bgPath="Battlefields/"+bgId+".png";
        Texture bgTex=new Texture(bgPath);
        this.background=new TextureRegion(bgTex);
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

    private Army getAllUnitsSortedByY() {
        Army p1 = battleEngine.getState().getPlayerOne().getArmy();
        Army p2 = battleEngine.getState().getPlayerTwo().getArmy();
        List<UnitStack> allUnits = new ArrayList<>();
        allUnits.addAll(p1.getUnits());
        allUnits.addAll(p2.getUnits());
        allUnits.sort(new Comparator<UnitStack>() {
            @Override
            public int compare(UnitStack u1, UnitStack u2) {
                return Integer.compare(
                    u2.getPosition().y(),
                    u1.getPosition().y()
                );
            }
        });
        return new Army(allUnits);
    }
    private void drawUnits() {
        batch.begin();
        drawArmySprites(getAllUnitsSortedByY());
        batch.end();

        drawUnitCountBadgeBackgrounds();
        drawUnitCountBadgeTexts();
    }

    private void drawArmySprites(Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive()) {
                drawUnitSprite(unit);
            }
        }
    }

    private void drawUnitSprite(UnitStack unit) {
        float hexHeight = BattleViewConfig.HEX_HEIGHT;
        float hexWidth = BattleViewConfig.HEX_WIDTH;
        float dx = 0;
        float dy = 0;
        FrameData frameData = unit.getAnimationEngine().nextFrame();
        int x = frameData.getX();
        int y = frameData.getY();
        int squareLength = frameData.getSize();
        dx += frameData.getDx();
        dy += frameData.getDy();

        TextureRegion texture = new TextureRegion(unitTextures.get(unit.getType()), x, y, squareLength, squareLength);
        int spriteSize = texture.getRegionWidth();

        if (frameData.isFlipX() || (unit.getOwner()==Player.PLAYER_TWO && !unit.getAnimationEngine().isBusy())) {
            texture.flip(true, false);
            dx -= spriteSize - hexWidth / 2f;
        }

        float size = spriteSize * 1.4f;

        if (frameData.isMoving()) {
            batch.draw(texture, - hexHeight / 2f + dx, - hexHeight / 4f + dy, size, size);
            return;
        }
        Vector2 center = BattlefieldGeometry.positionToScreen(unit.getPosition());
        batch.draw(texture, center.x - hexHeight / 2f + dx, center.y - hexHeight / 4f + dy, size, size);
    }

    private void drawUnitCountBadgeBackgrounds() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.04f, 0.03f, 0.9f);
        drawArmyCountBadgeBackgrounds(battleEngine.getState().getPlayerOne().getArmy());
        drawArmyCountBadgeBackgrounds(battleEngine.getState().getPlayerTwo().getArmy());
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.85f, 0.78f, 0.45f, 1f);
        drawArmyCountBadgeBackgrounds(battleEngine.getState().getPlayerOne().getArmy());
        drawArmyCountBadgeBackgrounds(battleEngine.getState().getPlayerTwo().getArmy());
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
        drawArmyCountBadgeTexts(battleEngine.getState().getPlayerOne().getArmy());
        drawArmyCountBadgeTexts(battleEngine.getState().getPlayerTwo().getArmy());
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

        float dx = 0;
        float dy = 0;
        FrameData frameData = unit.getAnimationEngine().nextFrame();
        if (frameData.isMoving()) {
            dx += frameData.getDx();
            dy += frameData.getDy();
        } else {
            dx += center.x;
            dy += center.y;
        }

        float badgeX = dx;
        float badgeY = dy - 14*BattleViewConfig.HEX_HEIGHT/32f;
        if (unit.getOwner() == Player.PLAYER_TWO) {
            badgeX-=BattleViewConfig.UNIT_COUNT_BADGE_WIDTH;
        }

        return new Vector2(badgeX, badgeY);
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
        UnitStack activeUnit = battleEngine.getActiveUnit();
        if (activeUnit == null || !activeUnit.isAlive()) {
            return;
        }

        BattleState state = battleEngine.getState();
        BattleField field = state.getField();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.08f, 0.45f);
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Position target = new Position(col, row);

                if (battleEngine.findUnitAt(target)!=null) {
                    continue;
                }

                if (BattlePathFinder.canReach(state, activeUnit, target)) {
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
