package io.github.heroes.model.state;

import static io.github.heroes.model.state.CastleType.CASTLE;

public enum UnitType {
    PIKEMAN(10, 4, 5, 1, 3, 4, "Pikeman", CASTLE),

    ARCHER(8, 6, 3, 2, 4, 4, "Archer", CASTLE),

    GRIFFIN(25, 8, 8, 3, 6, 6, "Griffin", CASTLE),

    ANGEL(200, 20, 20, 50, 50, 12, "Angel", CASTLE);

    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int minDamage;
    private final int maxDamage;
    private final int speed;
    private final String name;
    private final CastleType castleType;

    UnitType(
        int maxHp,
        int attack,
        int defense,
        int minDamage,
        int maxDamage,
        int speed,
        String name,
        CastleType castleType
    ) {
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.speed = speed;
        this.name = name;
        this.castleType = castleType;
    }

    public String getName() { return name; }
    public CastleType getCastleType() { return castleType; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getMinDamage() { return minDamage; }
    public int getMaxDamage() { return maxDamage; }
    public int getSpeed() { return speed; }
}
