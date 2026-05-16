package io.github.heroes.combat;

import io.github.heroes.model.BattleState;
import io.github.heroes.model.UnitStack;
import java.util.List;

public class BattleController {
    private final BattleState state;
    private final TurnQueue turnQueue;

    public BattleController(BattleState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        this.state = state;
        this.turnQueue = new TurnQueue(state);
    }

    public void performAction(BattleAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }

        if (state.isFinished()) {
            throw new IllegalStateException("Battle is already finished");
        }

        action.execute(state);

        if (!state.isFinished()) {
            if (action instanceof WaitAction) {
                turnQueue.waitCurrentUnit();
            } else {
                turnQueue.nextTurn();
            }
        }
    }

    public BattleState getState() {
        return state;
    }

    public UnitStack getActiveUnit() {
        return state.getActiveUnit();
    }

    public int getRound() {
        return state.getRound();
    }

    public List<TurnQueueEntry> getTurnQueueOrder() {
        return turnQueue.getQueue();
    }
}
