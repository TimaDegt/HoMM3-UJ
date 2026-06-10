package io.github.heroes.view.Battle.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.Position;
import io.github.heroes.view.Battle.BattlefieldGeometry;

import java.util.ArrayList;
import java.util.List;

import static io.github.heroes.view.Battle.BattleViewConfig.ANIMATION_SPEED;
import static io.github.heroes.view.Battle.BattleViewConfig.MOVEMENT_DURATION;

public class Animation {
    private final float MOVEMENT_SPEED = 1f/6f;
    private final float ANIMATION_SPEED = 1f/12f;
    private final int MOVE_FRAMES = 5;

    private final AnimParams params;

    private AnimParams.AnimType animType = AnimParams.AnimType.IDLE;
    private float frameIndex = 0f;

    public Animation(AnimParams params) {
        this.params = params;
    }

    public boolean finished() {
        return animType == AnimParams.AnimType.IDLE
            || animType == AnimParams.AnimType.DEAD;
    }

    public boolean isDead() {
        return animType == AnimParams.AnimType.DEAD;
    }

    List<Position> movementPath = null;

    private float moveFramesShown = 0;
    public void startMovement(List<Position> path) {
        if (path == null || path.size() < 1) {
            throw new IllegalArgumentException("path must be non-empty");
        }
        if (path.size() == 1) return;
        this.movementPath = new ArrayList<>(path);
        this.animType = AnimParams.AnimType.MOVE;
        this.frameIndex = 0;
        this.moveFramesShown = 0;
    }

    private boolean needsFlip = false;
    private Position currentPosition = null;
    public void startAttack(UnitSnapshot attacker, UnitSnapshot target) {
        //Gdx.app.log("ANIM_DEBUG", "Start attack");
        Position attackerPos = attacker.position();
        Position targetPos = target.position();
        needsFlip = BattlefieldGeometry.positionToScreen(attackerPos).x >
            BattlefieldGeometry.positionToScreen(targetPos).x;
        currentPosition = attackerPos;
        this.animType = AnimParams.AnimType.ATTACK;
        this.frameIndex = 0;
    }

    public void startReceiveDamage() {
        this.animType = AnimParams.AnimType.RECEIVEDMG;
        this.frameIndex = 0;
    }

    public void startDeath() {
        this.animType = AnimParams.AnimType.DEATH;
        this.frameIndex = 0;
    }

    public boolean isBusy() {
        return animType != AnimParams.AnimType.IDLE;
    }

    public void setFree() {
        animType = AnimParams.AnimType.IDLE;
        frameIndex = 0f;
    }

    public FrameData nextFrame() {
        int coordinate = params.currentCoordinate(animType);
        int length = params.currentLength(animType);
        int size = params.getSize();
        if (animType == AnimParams.AnimType.IDLE) {
            return new FrameData(
                0,
                coordinate,
                size,
                params.getOffsetX(),
                params.getOffsetY(),
                false,
                false
            );
        }
        if (animType == AnimParams.AnimType.DEAD) {
            return new FrameData(
                (Math.max(length, 1) - 1) * size,
                coordinate,
                size,
                params.getOffsetX(),
                params.getOffsetY(),
                false,
                false
            );
        }
        int floorIndex = 0;
        while (floorIndex + 1 <= frameIndex) ++floorIndex;
        float dx = params.getOffsetX(), dy = params.getOffsetY();
        boolean flipX = false;
        if (animType == AnimParams.AnimType.MOVE) {
            Position currentTile = movementPath.get(0);
            Position nextTile = movementPath.get(1);
            Vector2 currentPos = BattlefieldGeometry.positionToScreen(currentTile);
            Vector2 nextPos = BattlefieldGeometry.positionToScreen(nextTile);
            if (nextPos.x < currentPos.x) flipX = true;
            dx += (nextPos.x - currentPos.x) * moveFramesShown / (1f * MOVE_FRAMES) + currentPos.x;
            dy += (nextPos.y - currentPos.y) * moveFramesShown / (1f * MOVE_FRAMES) + currentPos.y;
        }
        if (animType == AnimParams.AnimType.ATTACK) {
            dx += BattlefieldGeometry.positionToScreen(currentPosition).x;
            dy += BattlefieldGeometry.positionToScreen(currentPosition).y;
            flipX = needsFlip;
        }
        if (flipX) dx -= 2*params.getOffsetX();
        return new FrameData(
            size * floorIndex,
            coordinate,
            size,
            dx,
            dy,
            flipX,
            animType == AnimParams.AnimType.MOVE || animType == AnimParams.AnimType.ATTACK
        );
    }

    public void updatik(float delta) {
        if (animType == AnimParams.AnimType.IDLE || animType == AnimParams.AnimType.DEAD) return;

        frameIndex += ANIMATION_SPEED;
            //x(animType == AnimParams.AnimType.MOVE ? MOVEMENT_SPEED : ANIMATION_SPEED);
        moveFramesShown += MOVEMENT_SPEED;

        int length = params.currentLength(animType);

        if (animType == AnimParams.AnimType.MOVE) {
            if (moveFramesShown >= MOVE_FRAMES) {
                moveFramesShown -= MOVE_FRAMES;
                movementPath.remove(0);
                if (movementPath.size() <= 1) {
                    movementPath = null;
                    animType = AnimParams.AnimType.IDLE;
                }
            }
            if (movementPath == null) {
                if (frameIndex >= length) {
                    animType = AnimParams.AnimType.IDLE;
                    frameIndex = 0f;
                    needsFlip = false;
                    currentPosition = null;
                }
                return;
            }
            if (params.isFlying()) {
                if (frameIndex >= length-2) {
                    frameIndex = 3f;
                }
            }
            if (frameIndex >= length) frameIndex = 0f;
            return;
        }

        if (frameIndex >= length) {
            if (animType == AnimParams.AnimType.DEATH) {
                animType = AnimParams.AnimType.DEAD;
                frameIndex = 0f;
                return;
            }
            if (animType == AnimParams.AnimType.MOVE) {
                frameIndex = 0f;
                return;
            }
            animType = AnimParams.AnimType.IDLE;
            frameIndex = 0f;
            needsFlip = false;
            currentPosition = null;
        }
    }
}
