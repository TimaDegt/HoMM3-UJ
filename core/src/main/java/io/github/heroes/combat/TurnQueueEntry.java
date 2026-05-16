package io.github.heroes.combat;

import io.github.heroes.model.UnitStack;

public class TurnQueueEntry {
    private final UnitStack unitStack;
    private final int round;
    private boolean wait;

    public TurnQueueEntry(UnitStack unitStack, int round, boolean wait) {
        this.unitStack = unitStack;
        this.round = round;
        this.wait=wait;
    }

    public void setWait(boolean wait){ this.wait=wait;}

    public UnitStack getUnitStack() {
        return unitStack;
    }

    public int getRound() {
        return round;
    }

    public boolean gatWait() {return wait;}
}
