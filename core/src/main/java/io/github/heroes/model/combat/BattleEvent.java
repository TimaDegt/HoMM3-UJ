package io.github.heroes.model.combat;

import io.github.heroes.model.spellBook.SpellBook;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.List;

public interface BattleEvent {

    record UnitMoved(
        UnitStack unit,
        Position from,
        Position to,
        List<Position> path
    ) implements BattleEvent {}

    record UnitAttacked(UnitStack attacker, UnitStack target) implements BattleEvent {}

    record UnitWaited(UnitStack unit)implements BattleEvent{}

    record UnitDamaged(
        UnitStack unit,
        int damage,
        int remainingCount,
        int remainingHp
    ) implements BattleEvent {
    }

    record UnitDied(UnitStack unit, Position position) implements BattleEvent {
    }

    record UnitDefended(UnitStack unit) implements BattleEvent {
    }
    record OpenSpellBook(
        SpellBook book
    ) implements  BattleEvent {

    }

    record BattleFinished(Player winner) implements BattleEvent {
    }
}
