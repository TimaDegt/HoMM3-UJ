package io.github.heroes.model;

import static io.github.heroes.model.CastleType.*;

public enum UnitType {
    PIKEMAN(10, 4, 5, 1, 3, "Pikeman", CASTLE),
    ARCHER(8, 6, 3, 2, 4, "Archer", CASTLE),
    GRIFFIN(25, 8, 8, 3, 6, "Griffin", CASTLE),;

    public final int maxHp;
    public final int attack;
    public final int defense;
    public final int damage;
    public final int speed;
    private final String name;
    private final CastleType castleType;

    UnitType(int maxHp, int attack, int defense, int damage, int speed, String name, CastleType castleType) {
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.speed = speed;
        this.name = name;
        this.castleType = castleType;
    }

    public String getName() { return name; }
    public CastleType getCastleType() { return castleType; }
}
