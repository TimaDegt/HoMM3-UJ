package io.github.heroes.combat;

import io.github.heroes.model.BattleState;
import io.github.heroes.model.UnitStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TurnQueue {
    private static final int MAX_ROUNDS = 100;

    private final BattleState state;
    private final List<TurnQueueEntry> queue;

    public TurnQueue(BattleState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        this.state = state;
        this.queue = new ArrayList<>();

        buildQueue();
        updateActiveUnit();
    }

    public UnitStack getCurrentUnit() {
        if (queue.isEmpty()) return null;

        return queue.get(0).getUnitStack();
    }

    public void nextTurn() {
        if (queue.isEmpty()) return;

        queue.remove(0);

        skipDeadUnits();
        updateActiveUnit();
    }

    public void waitCurrentUnit() {
        if (queue.isEmpty()) return;

        TurnQueueEntry waitingEntry = queue.remove(0);
        waitingEntry.setWait(true);
        queue.add(waitingEntry);
        skipDeadUnits();
        sortQueue();
        updateActiveUnit();
    }

    public void sortQueue(){
        queue.sort(Comparator
            .comparingInt(TurnQueueEntry::getRound)
                .thenComparing(
                    Comparator.comparing(TurnQueueEntry::gatWait)
                )
            .thenComparing(
                Comparator.comparingInt(this::getSpeedPriority)
            ));
    }

    private int getSpeedPriority(TurnQueueEntry entry) {
        if (entry.gatWait()) {
            return entry.getUnitStack().getType().speed;
        }

        return -entry.getUnitStack().getType().speed;
    }

    private void buildQueue() {
        queue.clear();

        List<UnitStack> units = new ArrayList<>();
        units.addAll(state.getPlayerOne().getArmy().getAliveUnits());
        units.addAll(state.getPlayerTwo().getArmy().getAliveUnits());

        for (int round = 1; round <= MAX_ROUNDS; round++) {
            for (UnitStack unit : units) {
                queue.add(new TurnQueueEntry(unit, round,false));
            }
        }

        sortQueue();

    }

    private void skipDeadUnits() {
        int currentIndex = 0;
        while (!queue.isEmpty() && currentIndex < queue.size()) {
            if (!queue.get(currentIndex).getUnitStack().isAlive())queue.remove(currentIndex);
            else currentIndex++;
        }
    }


    private void updateActiveUnit() {
        state.setActiveUnit(getCurrentUnit());
    }

    public List<TurnQueueEntry> getQueue() {
        if (queue.isEmpty()) {
            return List.of();
        }

        return queue;
    }
}
