package io.github.heroes.model.snapshot;

import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitType;
import io.github.heroes.model.state.unit.stack.UnitStack;

public record UnitSnapshot(
    long id,
    UnitType type,
    int count,
    int currentHp,
    int maxHp,
    Position position,
    Player owner,
    int attack,
    int baseAttack,
    int defense,
    int baseDefense,
    int speed,
    boolean alive
) {
    public static UnitSnapshot from(UnitStack unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");

        return new UnitSnapshot(
            unit.getId(),
            unit.getType(),
            unit.getCount(),
            unit.getCurrentHp(),
            unit.getMaxHp(),
            unit.getPosition(),
            unit.getOwner(),
            unit.getAttack(),
            unit.getBaseAttack(),
            unit.getDefense(),
            unit.getBaseDefense(),
            unit.getSpeed(),
            unit.isAlive()
        );
    }
}
