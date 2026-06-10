package io.github.heroes.model.combat;

import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;

import java.util.List;

public interface BattleEvent {

    record UnitMoved(
        UnitSnapshot unit,
        Position from,
        Position to,
        List<Position> path
    ) implements BattleEvent {
        public UnitMoved {
            path = List.copyOf(path);
        }
    }

    record UnitAttacked(
        UnitSnapshot attacker,
        UnitSnapshot target,
        boolean animateAttacker
    ) implements BattleEvent {
        public UnitAttacked(UnitSnapshot attacker, UnitSnapshot target) {
            this(attacker, target, true);
        }
    }

    record UnitWaited(UnitSnapshot unit) implements BattleEvent {}

    record UnitDamaged(
        UnitSnapshot unit,
        int damage,
        int remainingCount,
        int remainingHp
    ) implements BattleEvent {}

    record UnitDied(UnitSnapshot unit, Position position) implements BattleEvent {}

    record UnitDefended(UnitSnapshot unit) implements BattleEvent {}

    record SpellSelected(int spellIndex) implements BattleEvent {}

    record BattleFinished(Player winner) implements BattleEvent {}
}
