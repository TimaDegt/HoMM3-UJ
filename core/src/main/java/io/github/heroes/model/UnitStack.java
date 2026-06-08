package io.github.heroes.model;

import com.badlogic.gdx.math.Vector2;
import io.github.heroes.combat.BattlePathFinder;
import io.github.heroes.model.anim.AnimParams;
import io.github.heroes.model.anim.Animation;
import io.github.heroes.model.anim.Directions;
import io.github.heroes.model.anim.SpriteCoordinate;
import io.github.heroes.view.BattlefieldGeometry;

import java.util.ArrayList;
import java.util.List;

import static io.github.heroes.view.BattleViewConfig.MOVEMENT_FRAMES;
import static io.github.heroes.view.BattleViewConfig.MOVEMENT_SPEED;

public class UnitStack {
    private final UnitType type;
    private int count;
    private int currentHp;
    private Position position;
    private final Player owner;
    private boolean defending;
    private Animation animation;

    private final int maxCount;
    private int topUnitHp;
    private final int maxHp;

    private int bonusAttack = 0;
    private int bonusDefense = 0;
    private int bonusSpeed = 0;

    private boolean isBlessed = false;
    private boolean isCursed = false;

    public float deltaX = 0;
    public float deltaY = 0;
    private float moving = 0;
    private int movedFrames = 0;

    private void validatePositiveValue(int val) {
        if (val < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
    }

    public UnitStack(UnitType type, int count, Position position, Player owner) {
        this.type = type;
        this.count = count;
        this.currentHp = type.maxHp;
        this.position = position;
        this.owner = owner;
        this.defending = false;
        this.animation = new Animation(type.getAnimParams());
        this.maxCount = count;
        this.maxHp = type.maxHp;
        this.topUnitHp = type.maxHp;
    }

    public UnitType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public Position getPosition() {
        return position;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isAlive() {
        return count > 0;
    }

    public boolean isDefending() {
        return defending;
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    public void takeDamage(int damage) {
        validatePositiveValue(damage);

        int totalHp = (count - 1) * type.maxHp + currentHp;


        if (totalHp <= damage) {
            currentHp = 0;
            count = 0;
            animation.startAnimation(AnimParams.AnimType.DEATH);
        } else {
            currentHp = (totalHp - damage) % type.maxHp;
            if (currentHp == 0) currentHp = type.maxHp;
            count = (totalHp - damage - currentHp) / type.maxHp + 1;
            animation.startAnimation(AnimParams.AnimType.RECEIVEDMG);
        }
        topUnitHp = currentHp;
    }

    public void changePosition(Position newPosition) {
        this.position = newPosition;
    }

    private List<Position> path = new ArrayList<Position>();

    public SpriteCoordinate nextFrame() {
        boolean tmp = false;
        if (!path.isEmpty()) {
            if (movedFrames == MOVEMENT_FRAMES) {
                this.position = path.get(0);
                path.remove(0);
                movedFrames = 0;
                deltaX = 0;
                deltaY = 0;
                moving = 0f;
            }
            tmp = true;
        }
        if (path.isEmpty()) {
            if (tmp) animation.setFree();
        } else {
            Vector2 nextPos = BattlefieldGeometry.positionToScreen(path.get(0));
            Vector2 currentPos = BattlefieldGeometry.positionToScreen(this.position);
            float dX = nextPos.x - currentPos.x;
            float dY = nextPos.y - currentPos.y;
            deltaX += dX * MOVEMENT_SPEED;
            deltaY += dY * MOVEMENT_SPEED;
            moving += MOVEMENT_SPEED;
            movedFrames++;
            if (!animation.isBusy()) startAnimation(AnimParams.AnimType.MOVE);
        }
        return animation.nextFrame();
    }

    public void initiateMovement(List<Position> path) {
        this.path = path;
        startAnimation(AnimParams.AnimType.MOVE);
    }

    public void startAnimation(AnimParams.AnimType animType) {
        animation.startAnimation(animType);
    }
    public boolean isMoving() {
        return (!path.isEmpty()) && (movedFrames != MOVEMENT_FRAMES) && (movedFrames != 0);
    }

    public Directions getMovementDirection() {
        Vector2 nextPos = BattlefieldGeometry.positionToScreen(path.get(0));
        Vector2 currentPos = BattlefieldGeometry.positionToScreen(this.position);
        float dX = nextPos.x - currentPos.x;
        if (dX > 0) return Directions.RIGHT;
        if (dX < 0) return Directions.LEFT;
        return (owner == Player.PLAYER_ONE) ? Directions.RIGHT : Directions.LEFT;
    }

        public void addAttackBuff(int amount) {
        this.bonusAttack += amount;
    }

    public void addDefenseBuff(int amount) {
        this.bonusDefense += amount;
    }

    public void addSpeedBuff(int amount) {
        this.bonusSpeed += amount;
    }

    public void lockDamageToMaximum() {
        this.isBlessed = true;
        this.isCursed = false;
    }

    public void lockDamageToMinimum() {
        this.isCursed = true;
        this.isBlessed = false;
    }

    public void clearAllBuffsAndDebuffs() {
        this.bonusAttack = 0;
        this.bonusDefense = 0;
        this.bonusSpeed = 0;
        this.isBlessed = false;
        this.isCursed = false;
    }
    public void heal(int amount) {
        if (count <= 0) return;
        topUnitHp += amount;

        if (topUnitHp > maxHp) {
            topUnitHp = maxHp;
        }
    }

    public void resurrect(int totalHpToRestore) {
        if (count == maxCount && topUnitHp == maxHp) return;

        int missingHpOnTopUnit = maxHp - topUnitHp;

        if (totalHpToRestore <= missingHpOnTopUnit) {
            topUnitHp += totalHpToRestore;
            return;
        }

        totalHpToRestore -= missingHpOnTopUnit;
        topUnitHp = maxHp;

        int unitsToResurrect = totalHpToRestore / maxHp;
        int remainderHp = totalHpToRestore % maxHp;

        count += unitsToResurrect;

        if (count >= maxCount) {
            count = maxCount;
            topUnitHp = maxHp;
        } else if (remainderHp > 0) {
            count++;
            topUnitHp = remainderHp;
        }

    }


}
