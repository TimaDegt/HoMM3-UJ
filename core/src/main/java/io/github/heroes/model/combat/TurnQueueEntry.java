package io.github.heroes.model.combat;

import io.github.heroes.model.state.unit.stack.UnitStack;

public class TurnQueueEntry {
    private final UnitStack unitStack;
    private final int round;
    private boolean wait;

    public TurnQueueEntry(UnitStack unitStack, int round, boolean wait) {
        this.unitStack = unitStack;
        this.round = round;
        this.wait=wait;
    }

    void setWait(boolean wait){ this.wait=wait;}

    public UnitStack getUnitStack() {
        return unitStack;
    }

    public int getRound() {
        return round;
    }

    public boolean isWaiting() {return wait;}

    public boolean gatWait() {return isWaiting();}
}
