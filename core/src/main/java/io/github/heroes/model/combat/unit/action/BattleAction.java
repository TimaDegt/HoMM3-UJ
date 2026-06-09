package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.state.BattleState;

import java.util.List;

public interface BattleAction {
    List<BattleEvent> execute(BattleState state);

    default boolean endsTurn() {
        return true;
    }
}
