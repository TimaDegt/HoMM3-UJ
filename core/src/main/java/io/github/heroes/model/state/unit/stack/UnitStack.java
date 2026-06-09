package io.github.heroes.model.state.unit.stack;

import com.badlogic.gdx.Gdx;
import io.github.heroes.model.combat.unit.action.BattleAction;
import io.github.heroes.model.combat.unit.action.MoveAndAttackAction;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitType;

import java.util.concurrent.ThreadLocalRandom;

public abstract class UnitStack {
    private final UnitType type;
    private int count;
    private int currentHp;
    private Position position;
    private final Player owner;
    private boolean defending;

    private final int maxCount;
    private int topUnitHp;
    private final int maxHp;

    private int maxDamage;
    private int minDamage;

    private final int speed;
    private final int attack;
    private final int defense;

    private int bonusAttack = 0;
    private int bonusDefense = 0;
    private int bonusSpeed = 0;

    private boolean isBlessed = false;
    private boolean isCursed = false;

    private void validatePositiveValue(int val) {
        if (val < 0) throw new IllegalArgumentException("Value cannot be negative");
    }

    protected UnitStack(UnitType type, int count, Position position, Player owner) {
        this.type = type;
        this.count = count;
        this.currentHp = type.getMaxHp();
        this.position = position;
        this.owner = owner;
        this.defending = false;
        this.maxCount = count;
        this.maxHp = type.getMaxHp();
        this.topUnitHp = type.getMaxHp();
        this.minDamage = type.getMinDamage();
        this.maxDamage = type.getMaxDamage();
        this.speed = type.getSpeed();
        this.attack = type.getAttack();
        this.defense = type.getDefense();
    }

    public BattleAction createAttackAction(UnitStack target, Position attackPosition) {
        if (target == null) throw new IllegalArgumentException("Target cannot be null");
        if (attackPosition == null) return null;
        return new MoveAndAttackAction(this, attackPosition, target);
    }

    public boolean canAttackWithoutMoving(UnitStack target) {
        return false;
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

        int totalHp = (count-1) * maxHp + currentHp;

        if (totalHp<=damage){
            currentHp=0;
            count=0;
        } else {
            currentHp = (totalHp - damage) % maxHp;
            if (currentHp == 0) currentHp = maxHp;
            count = (totalHp - damage - currentHp) / maxHp + 1;
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
    public boolean isBlessed() {
        return isBlessed;
    }
    public boolean isCursed() {
        return isCursed;
    }
    public int getBonusAttack(){
        return this.bonusAttack;
    }
    public int getBonusDefense(){
        return this.bonusDefense;
    }
    public int getBonusSpeed(){
        return this.bonusSpeed;
    }

    public int calculateDamageRoll() {
        if (!isAlive()) {
            return 0;
        }

        int totalDamage = 0;
        for(int it = 0; it < count; it++) {
            int singleUnitDamage;
            if (isBlessed) {
                singleUnitDamage = maxDamage;
            } else if (isCursed) {
                singleUnitDamage = minDamage;
            } else {
                singleUnitDamage = ThreadLocalRandom.current().nextInt(
                    minDamage,
                    maxDamage + 1
                );
            }
            totalDamage += singleUnitDamage;
        }
        Gdx.app.log("DEBUG", "Rolled " + totalDamage + " damage");

        return totalDamage;
    }
    public int getSpeed() {
        return Math.max(0, speed + bonusSpeed);
    }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack + bonusAttack; }
    public int getDefense() { return defense + bonusDefense; }
    public int getBaseAttack() { return attack; }
    public int getBaseDefense() { return defense; }

}
