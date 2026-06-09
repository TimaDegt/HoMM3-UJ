package io.github.heroes.view.Battle.animation;

import com.badlogic.gdx.math.Vector2;
import io.github.heroes.model.state.Position;
import io.github.heroes.view.Battle.BattlefieldGeometry;

import java.util.List;

import static io.github.heroes.view.Battle.BattleViewConfig.ANIMATION_SPEED;
import static io.github.heroes.view.Battle.BattleViewConfig.MOVEMENT_SPEED;

public class Animation {
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

    List<Position> movementPath = null;

    public void startMovement(List<Position> path) {
        if (path == null || path.size() < 1) {
            throw new IllegalArgumentException("path must be non-empty");
        }
        if (path.size() == 1) return;
        this.movementPath = path;
        this.animType = AnimParams.AnimType.MOVE;
        this.frameIndex = 0;
    }

    public void startAttack() {
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
                size
            );
        }
        if (animType == AnimParams.AnimType.DEAD) {
            return new FrameData(
                (length - 1) * size,
                coordinate,
                size
            );
        }
        int floorIndex = 0;
        while (floorIndex + 1 <= frameIndex) ++floorIndex;
        float dx = 0f, dy = 0f;
        boolean flipX = false;
        if (animType == AnimParams.AnimType.MOVE) {
            Position currentTile = movementPath.get(0);
            Position nextTile = movementPath.get(1);
            Vector2 currentPos = BattlefieldGeometry.positionToScreen(currentTile);
            Vector2 nextPos = BattlefieldGeometry.positionToScreen(nextTile);
            if (nextPos.x < currentPos.x) flipX = true;
            dx = (nextPos.x - currentPos.x) * floorIndex / (1f * length) + currentPos.x;
            dy = (nextPos.y - currentPos.y) * floorIndex / (1f * length) + currentPos.y;
        }
        return new FrameData(
            size * floorIndex,
            coordinate,
            size,
            dx,
            dy,
            flipX,
            true
        );
    }

    public void updatik(float delta) {
        frameIndex += delta / (animType == AnimParams.AnimType.MOVE ? MOVEMENT_SPEED : ANIMATION_SPEED);
        int length = params.currentLength(animType);
        if (frameIndex >= length) {
            if (animType == AnimParams.AnimType.DEATH) {
                animType = AnimParams.AnimType.DEAD;
                frameIndex = 0f;
                return;
            }
            if (animType == AnimParams.AnimType.MOVE) {
                movementPath.remove(0);
                if (movementPath.size() <= 1) {
                    movementPath = null;
                    animType = AnimParams.AnimType.IDLE;
                }
                frameIndex = 0f;
                return;
            }
            animType = AnimParams.AnimType.IDLE;
            frameIndex = 0f;
        }
    }
}
