package io.github.heroes.view.Battle.Render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.view.Battle.cursor.Cursor;
import io.github.heroes.view.Battle.cursor.CursorType;
import io.github.heroes.control.BattleController;
import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.UnitType;
import io.github.heroes.view.Battle.BattleViewConfig;
import io.github.heroes.view.Battle.BattlefieldGeometry;
import io.github.heroes.view.Battle.animation.BattleAnimationPlayer;
import io.github.heroes.view.Battle.animation.FrameData;
import io.github.heroes.view.spellBook.SpellBookDisplay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.heroes.view.Battle.BattleViewConfig.CURSOR_SCALE;

public class BattleRenderer {
    private final BattleController battleController;
    private final BattleAnimationPlayer animationPlayer;
    private final FieldRenderer fieldRenderer;
    private final UnitBadgeRenderer unitBadgeRenderer;
    private final SpriteBatch batch;
    private final Map<UnitType, TextureRegion> unitTextures;
    private final Map<CursorType, TextureRegion> cursorTextures;
    private final SpellBookDisplay spellBookRenderer;

    private TextureRegion background;
    private Runnable actionAnimationFinished;
    private List<BattleEvent> events;

    public BattleRenderer(
        BattleController battleController,
        BattleAnimationPlayer animationPlayer
    ) {
        this.battleController = battleController;
        this.animationPlayer = animationPlayer;
        this.fieldRenderer = new FieldRenderer(battleController);
        this.unitBadgeRenderer = new UnitBadgeRenderer(battleController, animationPlayer);
        this.batch = new SpriteBatch();
        this.unitTextures = new HashMap<>();
        this.cursorTextures = new HashMap<>();
        this.spellBookRenderer = new SpellBookDisplay();

        loadTextures();
    }

    private void initGraphics() {
        for (UnitType type : UnitType.values()) {
            String townFolder = type.getCastleType().getName();
            String unitName = type.getName();
            String unitPath = "Units/" + townFolder + "/" + unitName + "_spritesheet.png";

            Texture texture = new Texture(Gdx.files.internal(unitPath));
            unitTextures.put(type, new TextureRegion(texture));
        }
        for (CursorType cursorType : CursorType.values()) {
            if (cursorType == CursorType.NONE) continue;
            String path = "Icons/Cursors/" + cursorType.getName() + ".png";
            Texture texture = new Texture(Gdx.files.internal(path));
            cursorTextures.put(cursorType, new TextureRegion(texture));
        }
    }

    private void drawCustomCursor() {
        CursorType cursorType = Cursor.getCustomCursor(battleController);
        if (cursorType == CursorType.NONE) {
            Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
            return;
        }
        Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.None);
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        TextureRegion regionToDraw = cursorTextures.get(cursorType);
        if (regionToDraw == null) return;

        float scaledWidth = regionToDraw.getRegionWidth() * CURSOR_SCALE;
        float scaledHeight = regionToDraw.getRegionHeight() * CURSOR_SCALE;
        batch.begin();
        batch.draw(
            regionToDraw,
            mouseX + cursorType.offX * CURSOR_SCALE,
            mouseY - scaledHeight + cursorType.offY * CURSOR_SCALE,
            scaledWidth,
            scaledHeight
        );
        batch.end();
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
        updateActionAnimation(delta);
        if (spellBookRenderer.isActive()) {
            batch.begin();
            spellBookRenderer.render(batch);
            batch.end();
        }
        if (noAnimations) drawCustomCursor();
    }

    public void playActionAnimation(ActionResult result, Runnable onFinished) {
        if (result == null) throw new IllegalArgumentException("Action result cannot be null");
        if (onFinished == null) throw new IllegalArgumentException("Completion callback cannot be null");

        this.events = new ArrayList<>(result.events());
        startNextEventAnimation();
        actionAnimationFinished = onFinished;
    }

    private void startNextEventAnimation() {
        while (events != null && !events.isEmpty()) {
            BattleEvent event = events.get(0);

            if (event instanceof BattleEvent.UnitMoved move) {
                animationPlayer.getAnimationEngine(move.unit()).startMovement(move.path());
            } else if (event instanceof BattleEvent.UnitAttacked attack) {
                animationPlayer.getAnimationEngine(attack.attacker()).startAttack();
            } else if (event instanceof BattleEvent.UnitDamaged damaged) {
                animationPlayer.getAnimationEngine(damaged.unit()).startReceiveDamage();
            } else if (event instanceof BattleEvent.UnitDied death) {
                animationPlayer.getAnimationEngine(death.unit()).startDeath();
            } else if (event instanceof BattleEvent.OpenSpellBook) {
                spellBookRenderer.setActive(true);
            } else {
                events.remove(0);
                continue;
            }
            break;
        }
    }

    private boolean allAnimationsFinished() {
        boolean allAnimationsFinished = true;
        for (UnitSnapshot unit : battleController.getBattleSnapshot().units()) {
            allAnimationsFinished &= animationPlayer.getAnimationEngine(unit).finished();
        }
        allAnimationsFinished &= !spellBookRenderer.isAnimating();
        return allAnimationsFinished;
    }

    private void updateActionAnimation(float delta) {
        if (actionAnimationFinished == null) return;
        if (spellBookRenderer.isActive()) return;

        if (!allAnimationsFinished()) {
            for (UnitSnapshot unit : battleController.getBattleSnapshot().units()) {
                animationPlayer.getAnimationEngine(unit).updatik(delta);
            }
            return;
        }
        if (events != null && !events.isEmpty()) events.remove(0);
        startNextEventAnimation();
        if (events != null && !events.isEmpty()) return;

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

        int backgroundId = MathUtils.random(0, 9);
        String backgroundPath = "Battlefields/" + backgroundId + ".png";
        background = new TextureRegion(new Texture(backgroundPath));
    }

    private List<UnitSnapshot> getAllUnitsSortedByY() {
        List<UnitSnapshot> allUnits = new ArrayList<>(battleController.getBattleSnapshot().units());
        allUnits.sort(Comparator.comparingInt((UnitSnapshot unit) -> unit.position().y()).reversed());
        return allUnits;
    }

    private void drawUnits() {
        batch.begin();
        drawUnitSprites(getAllUnitsSortedByY());
        batch.end();
        unitBadgeRenderer.render();
    }

    private void drawUnitSprites(List<UnitSnapshot> units) {
        for (UnitSnapshot unit : units) {
            if (unit.alive()
                || hasPendingVisualEvent(unit)
                || animationPlayer.getAnimationEngine(unit).isDead()) {
                drawUnitSprite(unit);
            }
        }
    }

    private boolean hasPendingVisualEvent(UnitSnapshot unit) {
        if (events == null) return false;
        for (BattleEvent event : events) {
            if (event instanceof BattleEvent.UnitAttacked attack
                && (attack.attacker().id() == unit.id() || attack.target().id() == unit.id())) {
                return true;
            }
            if (event instanceof BattleEvent.UnitDamaged damaged
                && damaged.unit().id() == unit.id()) return true;
            if (event instanceof BattleEvent.UnitDied died
                && died.unit().id() == unit.id()) return true;
        }
        return false;
    }

    private void drawUnitSprite(UnitSnapshot unit) {
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

        TextureRegion texture = new TextureRegion(
            unitTextures.get(unit.type()),
            x,
            y,
            squareLength,
            squareLength
        );
        int spriteSize = texture.getRegionWidth();

        boolean flipX = frameData.isMoving()
            ? frameData.isFlipX()
            : unit.owner() == Player.PLAYER_TWO;
        if (flipX) {
            texture.flip(true, false);
            dx -= spriteSize - hexWidth / 2f;
        }

        float size = spriteSize * 1.4f;

        if (frameData.isMoving()) {
            batch.draw(texture, -hexHeight / 2f + dx, -hexHeight / 4f + dy, size, size);
            return;
        }
        Vector2 center = BattlefieldGeometry.positionToScreen(unit.position());
        batch.draw(
            texture,
            center.x - hexHeight / 2f + dx,
            center.y - hexHeight / 4f + dy,
            size,
            size
        );
    }
}
