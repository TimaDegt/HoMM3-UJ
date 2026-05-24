package io.github.heroes.model;

import io.github.heroes.model.anim.AnimParams;
import io.github.heroes.model.anim.SpriteCoordinate;

import static io.github.heroes.model.CastleType.*;

public enum UnitType {
    PIKEMAN(10, 4, 5, 1, 3, "Pikeman", CASTLE, new AnimParams(
        125, 0, 5, 1, 6, 3, 6, -1, 0, 6, 6, 7, 7, 8
    )),
    ARCHER(8, 6, 3, 2, 4, "Archer", CASTLE, new AnimParams(
        125, 0, 6, 1, 6, 4, 6, 3, 6, 2, 6, 6, 5,  8
    )),
    GRIFFIN(25, 8, 8, 3, 6, "Griffin", CASTLE, new AnimParams(
        155, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0
    ));

    public final int maxHp;
    public final int attack;
    public final int defense;
    public final int damage;
    public final int speed;
    private final String name;
    private final CastleType castleType;
    private AnimParams animParams;

    UnitType(int maxHp, int attack, int defense, int damage, int speed, String name, CastleType castleType, AnimParams animParams) {
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.speed = speed;
        this.name = name;
        this.castleType = castleType;
        this.animParams = animParams;
    }

    public String getName() { return name; }
    public CastleType getCastleType() { return castleType; }
    public AnimParams getAnimParams() { return animParams; }
}
