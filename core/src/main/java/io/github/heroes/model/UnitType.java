package io.github.heroes.model;

public enum UnitType {
    PIKEMAN(10, 4, 5, 1, 3),
    ARCHER(8, 6, 3, 2, 4),
    GRIFFIN(25, 8, 8, 3, 6);

    public final int maxHp;
    public final int attack;
    public final int defense;
    public final int damage;
    public final int speed;

    UnitType(int maxHp, int attack, int defense, int damage, int speed) {
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.speed = speed;
    }
}
