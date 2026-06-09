package io.github.heroes.model.state;

import static io.github.heroes.model.state.CastleType.CASTLE;

public enum UnitType {
    PIKEMAN(10, 4, 5, 1, 3, 4, false, 0, "Pikeman", CASTLE),

    ARCHER(8, 6, 3, 2, 4, 4, true, 12, "Archer", CASTLE),

    GRIFFIN(25, 8, 8, 3, 6, 6, false, 0, "Griffin", CASTLE);

    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int minDamage;
    private final int maxDamage;
    private final int speed;
    private final boolean ranged;
    private final int ammo;
    private final String name;
    private final CastleType castleType;

    UnitType(
        int maxHp,
        int attack,
        int defense,
        int minDamage,
        int maxDamage,
        int speed,
        boolean ranged,
        int ammo,
        String name,
        CastleType castleType
    ) {
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.speed = speed;
        this.ranged = ranged;
        this.ammo = ammo;
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
    public boolean isRanged() { return ranged; }
    public int getAmmo() { return ammo; }
}
