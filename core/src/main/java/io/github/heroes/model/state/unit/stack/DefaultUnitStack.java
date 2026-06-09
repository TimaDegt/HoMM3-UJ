package io.github.heroes.model.state.unit.stack;

import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitType;

public class DefaultUnitStack extends UnitStack {
    public DefaultUnitStack(UnitType type, int count, Position position, Player owner) {
        super(requireDefaultType(type), count, position, owner);
    }

    private static UnitType requireDefaultType(UnitType type) {
        if (type == null) throw new IllegalArgumentException("Unit type cannot be null");
        if (type == UnitType.ARCHER) {
            throw new IllegalArgumentException("Archer unit requires ArcherUnitStack");
        }
        return type;
    }
}
