package io.github.heroes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Army {
    private final List<UnitStack> units;

    public Army() {
        this.units = new ArrayList<>();
    }

    public Army(List<UnitStack> units) {
        this.units = new ArrayList<>(units);
    }

    public void addUnit(UnitStack unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }

        units.add(unit);
    }

    public List<UnitStack> getUnits() {
        return Collections.unmodifiableList(units);
    }

    public List<UnitStack> getAliveUnits() {
        return units.stream()
            .filter(UnitStack::isAlive)
            .toList();
    }

    public boolean isDefeated() {
        return getAliveUnits().isEmpty();
    }
}
