package io.github.heroes.model.combat;

import io.github.heroes.model.state.BattleState;

import java.util.List;

public interface BattleAction {
    List<BattleEvent> execute(BattleState state);
}
