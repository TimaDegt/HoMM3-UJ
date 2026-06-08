package io.github.heroes.model.combat;

import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;

import java.util.ArrayList;
import java.util.List;

public class BattleEngine {
    private final BattleState state;
    private final TurnQueue turnQueue;

    public BattleEngine(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");

        this.state = state;
        this.turnQueue = new TurnQueue(state);
    }

    public ActionResult performAction(BattleAction action) {
        if (action == null) return ActionResult.failure();
        if (state.isFinished()) return ActionResult.failure();

        UnitStack previousUnit = state.getActiveUnit();
        List<BattleEvent> events;
        try {
            events = new ArrayList<>(action.execute(state));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ActionResult.failure();
        }

        if (events.isEmpty())return ActionResult.failure();

        if (state.isFinished()) {
            events.add(new BattleEvent.BattleFinished(state.getWinner()));
        } else {
            if (action instanceof WaitAction) turnQueue.waitCurrentUnit();
            else turnQueue.nextTurn();
        }

        return ActionResult.success(events);
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

    public boolean isPositionOccupied(Position position) {
        return state.getPlayerOne().getArmy().isPositionOccupied(position)
            || state.getPlayerTwo().getArmy().isPositionOccupied(position);
    }

    public UnitStack findUnitAt(Position position) {
        UnitStack unit = state.getPlayerOne().getArmy().findUnitAtPosition(position);
        if (unit != null) return unit;

        return state.getPlayerTwo().getArmy().findUnitAtPosition(position);
    }
}
