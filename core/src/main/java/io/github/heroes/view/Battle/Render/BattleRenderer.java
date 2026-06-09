package io.github.heroes.view.Battle.Render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.combat.cursor.Cursor;
import io.github.heroes.combat.cursor.CursorType;
import io.github.heroes.model.state.*;
import io.github.heroes.view.Battle.BattleViewConfig;
import io.github.heroes.view.Battle.BattlefieldGeometry;
import io.github.heroes.view.Battle.animation.BattleAnimationPlayer;
import io.github.heroes.view.Battle.animation.FrameData;

import java.util.*;

import static io.github.heroes.view.Battle.BattleViewConfig.CURSOR_SCALE;

public class BattleRenderer {
    private static final float ACTION_ANIMATION_DURATION = 1f;

    private final BattleEngine battleEngine;
    private final BattleAnimationPlayer animationPlayer;
    private final FieldRenderer fieldRenderer;
    private final UnitBadgeRenderer unitBadgeRenderer;
    private final SpriteBatch batch;
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
        BattleEngine battleEngine,
        BattleAnimationPlayer animationPlayer
    ) {
        this.battleEngine = battleEngine;
        this.animationPlayer = animationPlayer;
        this.fieldRenderer = new FieldRenderer(battleEngine);
        this.unitBadgeRenderer = new UnitBadgeRenderer(battleEngine, animationPlayer);
        this.batch = new SpriteBatch();
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

        boolean noAnimations = allAnimationsFinished();
        fieldRenderer.render(noAnimations);
        drawUnits();
        if (noAnimations) {
            drawCustomCursor();
        }
        updateActionAnimation(delta);
    }


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
                animationPlayer.getAnimationEngine(unit).startMovement(move.path());
            } else if (event instanceof BattleEvent.UnitAttacked attack) {
                UnitStack unit = attack.attacker();
                animationPlayer.getAnimationEngine(unit).startAttack();
            } else if (event instanceof BattleEvent.UnitDamaged receive) {
                UnitStack unit = receive.unit();
                animationPlayer.getAnimationEngine(unit).startReceiveDamage();
            } else if (event instanceof BattleEvent.UnitDied death) {
                UnitStack unit = death.unit();
                animationPlayer.getAnimationEngine(unit).startDeath();
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
            allAnimationsFinished &= animationPlayer.getAnimationEngine(unit).finished();
        }
        for (UnitStack unit : battleEngine.getState().getPlayerTwo().getArmy().getUnits()) {
            allAnimationsFinished &= animationPlayer.getAnimationEngine(unit).finished();
        }
        return allAnimationsFinished;
    }

    private void updateActionAnimation(float delta) {
        if (actionAnimationFinished == null) return;

        if (!allAnimationsFinished()) {
            for (UnitStack unit : battleEngine.getState().getPlayerOne().getArmy().getUnits()) {
                animationPlayer.getAnimationEngine(unit).updatik(delta);
            }
            for (UnitStack unit : battleEngine.getState().getPlayerTwo().getArmy().getUnits()) {
                animationPlayer.getAnimationEngine(unit).updatik(delta);
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
                animationPlayer.getAnimationEngine(unit).startMovement(move.path());
            } else if (event instanceof BattleEvent.UnitAttacked attack) {
                UnitStack unit = attack.attacker();
                animationPlayer.getAnimationEngine(unit).startAttack();
            } else if (event instanceof BattleEvent.UnitDamaged receive) {
                UnitStack unit = receive.unit();
                animationPlayer.getAnimationEngine(unit).startReceiveDamage();
            } else if (event instanceof BattleEvent.UnitDied death) {
                UnitStack unit = death.unit();
                animationPlayer.getAnimationEngine(unit).startDeath();
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

    public void dispose() {
        fieldRenderer.dispose();
        unitBadgeRenderer.dispose();
        batch.dispose();
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

        unitBadgeRenderer.render();
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
        FrameData frameData = animationPlayer.getAnimationEngine(unit).nextFrame();
        int x = frameData.getX();
        int y = frameData.getY();
        int squareLength = frameData.getSize();
        dx += frameData.getDx();
        dy += frameData.getDy();

        TextureRegion texture = new TextureRegion(unitTextures.get(unit.getType()), x, y, squareLength, squareLength);
        int spriteSize = texture.getRegionWidth();

        if (frameData.isFlipX() || (unit.getOwner()==Player.PLAYER_TWO && !animationPlayer.getAnimationEngine(unit).isBusy())) {
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

}
