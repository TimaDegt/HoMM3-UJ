package io.github.heroes.model.snapshot;

import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.ArrayList;
import java.util.List;

public record BattleSnapshot(
    int fieldWidth,
    int fieldHeight,
    List<UnitSnapshot> units,
    Long activeUnitId,
    Player winner,
    int round
) {
    public BattleSnapshot {
        units = List.copyOf(units);
    }

    public static BattleSnapshot from(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");

        List<UnitSnapshot> units = new ArrayList<>();
        for (UnitStack unit : state.getPlayerOne().getArmy().getUnits()) {
            units.add(UnitSnapshot.from(unit));
        }
        for (UnitStack unit : state.getPlayerTwo().getArmy().getUnits()) {
            units.add(UnitSnapshot.from(unit));
        }

        UnitStack activeUnit = state.getActiveUnit();
        return new BattleSnapshot(
            state.getField().getWidth(),
            state.getField().getHeight(),
            units,
            activeUnit == null ? null : activeUnit.getId(),
            state.getWinner(),
            state.getRound()
        );
    }

    public UnitSnapshot activeUnit() {
        if (activeUnitId == null) return null;
        for (UnitSnapshot unit : units) {
            if (unit.id() == activeUnitId) return unit;
        }
        return null;
    }

    public UnitSnapshot findUnitAt(Position position) {
        if (position == null) return null;
        for (UnitSnapshot unit : units) {
            if (unit.alive() && unit.position().equals(position)) return unit;
        }
        return null;
    }

    public boolean isPositionOccupied(Position position) {
        return findUnitAt(position) != null;
    }

    public boolean isInside(Position position) {
        return position != null
            && position.x() >= 0
            && position.x() < fieldWidth
            && position.y() >= 0
            && position.y() < fieldHeight;
    }

    public boolean isFinished() {
        return winner != null;
    }
}
