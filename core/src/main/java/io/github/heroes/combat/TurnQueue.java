package io.github.heroes.combat;

import io.github.heroes.model.BattleState;
import io.github.heroes.model.UnitStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TurnQueue {
    private final BattleState state;
    private final List<UnitStack> queue;
    private int currentIndex;

    public TurnQueue(BattleState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        this.state = state;
        this.queue = new ArrayList<>();
        this.currentIndex = 0;

        rebuildQueue();
        updateActiveUnit();
    }

    public UnitStack getCurrentUnit() {
        if (queue.isEmpty()) {
            return null;
        }

        return queue.get(currentIndex);
    }

    public void nextTurn() {
        if (queue.isEmpty()) {
            return;
        }

        currentIndex++;

        if (currentIndex >= queue.size()) {
            currentIndex = 0;
            state.nextRound();
            rebuildQueue();
        }

        skipDeadUnits();
        updateActiveUnit();
    }

    public void waitCurrentUnit() {
        if (queue.isEmpty()) return;

        UnitStack waitingUnit = queue.remove(currentIndex);
        queue.add(waitingUnit);

        if (currentIndex >= queue.size()) currentIndex = 0;

        skipDeadUnits();
        updateActiveUnit();
    }

    private void rebuildQueue() {
        queue.clear();

        queue.addAll(state.getPlayerOne().getArmy().getAliveUnits());
        queue.addAll(state.getPlayerTwo().getArmy().getAliveUnits());

        queue.sort(Comparator
            .comparingInt((UnitStack unit) -> unit.getType().speed)
            .reversed());
    }

    private void skipDeadUnits() {
        while (!queue.isEmpty() && !queue.get(currentIndex).isAlive()) {
            queue.remove(currentIndex);

            if (currentIndex >= queue.size()) {
                currentIndex = 0;
            }
        }
    }

    private void updateActiveUnit() {
        state.setActiveUnit(getCurrentUnit());
    }
}
