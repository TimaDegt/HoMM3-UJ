package io.github.heroes.model.state;

import io.github.heroes.anim.AnimParams;
import io.github.heroes.model.state.CastleType;

import static io.github.heroes.model.state.CastleType.CASTLE;

public enum UnitType {
    PIKEMAN(10, 4, 5, 1, 3, 4, false, 0, "Pikeman", CASTLE),

    ARCHER(8, 6, 3, 2, 4, 4, true, 12, "Archer", CASTLE),

    GRIFFIN(25, 8, 8, 3, 6, 6, false, 0, "Griffin", CASTLE);

    public final int maxHp;
    public final int attack;
    public final int defense;
    public final int minDamage;
    public final int maxDamage;
    public final int speed;
    private final boolean ranged;
    private final int ammo;
    private final String name;
    private final CastleType castleType;
    private final AnimParams animParams;

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
        this.animParams = null;
    }

    public String getName() { return name; }
    public CastleType getCastleType() { return castleType; }
    public AnimParams getAnimParams() { return animParams; }

    public boolean isRanged() {
        return ranged;
    }

    public int getAmmo() {
        return ammo;
    }
}
