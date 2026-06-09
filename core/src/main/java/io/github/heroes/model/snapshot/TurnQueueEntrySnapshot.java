package io.github.heroes.model.snapshot;

import io.github.heroes.model.combat.TurnQueueEntry;

public record TurnQueueEntrySnapshot(
    UnitSnapshot unit,
    int round,
    boolean waiting
) {
    public static TurnQueueEntrySnapshot from(TurnQueueEntry entry) {
        if (entry == null) throw new IllegalArgumentException("Queue entry cannot be null");
        return new TurnQueueEntrySnapshot(
            UnitSnapshot.from(entry.getUnitStack()),
            entry.getRound(),
            entry.isWaiting()
        );
    }
}
