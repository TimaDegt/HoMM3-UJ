package io.github.heroes.model;

public class UnitStack {
    private final UnitType type;
    private int count;
    private int currentHp;
    private Position position;
    private final Player owner;
    private boolean defending;

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

    }

    public void changePosition(Position newPosition){
        position = newPosition;
    }

}
