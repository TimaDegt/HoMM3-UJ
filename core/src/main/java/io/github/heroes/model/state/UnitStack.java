package io.github.heroes.model.state;


import io.github.heroes.anim.Animation;

public class UnitStack {
    private final UnitType type;
    private int count;
    private int currentHp;
    private Position position;
    private final Player owner;
    private boolean defending;

    private final int maxCount;
    private int topUnitHp;
    private final int maxHp;

    private int bonusAttack = 0;
    private int bonusDefense = 0;
    private int bonusSpeed = 0;

    private boolean isBlessed = false;
    private boolean isCursed = false;

    private final Animation animationEngine;

    private void validatePositiveValue(int val) {
        if (val < 0) throw new IllegalArgumentException("Value cannot be negative");
    }

    public UnitStack(UnitType type, int count, Position position, Player owner) {
        this.type = type;
        this.count = count;
        this.currentHp = type.maxHp;
        this.position = position;
        this.owner = owner;
        this.defending = false;
        this.maxCount = count;
        this.maxHp = type.maxHp;
        this.topUnitHp = type.maxHp;
        this.animationEngine = new Animation(this.type.getAnimParams());
    }

    public Animation getAnimationEngine() {
        return animationEngine;
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

    public boolean isDefending() {return defending;}

    public void setDefending(boolean defending) {this.defending = defending;}

    public void takeDamage(int damage){
        validatePositiveValue(damage);

        int totalHp = (count-1) * type.maxHp + currentHp;

        if (totalHp<=damage){
            currentHp=0;
            count=0;
        } else {
            currentHp = (totalHp - damage)% type.maxHp;
            if (currentHp == 0)currentHp=type.maxHp;
            count = (totalHp - damage - currentHp)/type.maxHp + 1;
        }
        topUnitHp = currentHp;
    }

    public void changePosition(Position newPosition){
        position=newPosition;
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
