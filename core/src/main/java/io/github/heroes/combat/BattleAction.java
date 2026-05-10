package io.github.heroes.combat;

import io.github.heroes.model.BattleState;

public interface BattleAction {
    void execute(BattleState state);
}
