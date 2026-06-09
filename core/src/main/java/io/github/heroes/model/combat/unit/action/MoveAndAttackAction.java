package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.combat.BattlePathFinder;
import io.github.heroes.model.combat.CombatResolver;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;

import java.util.ArrayList;
import java.util.List;

public class MoveAndAttackAction implements BattleAction {
    private final UnitStack attacker;
    private final Position attackPosition;
    private final UnitStack target;
    private final CombatResolver combatResolver;

    public MoveAndAttackAction(UnitStack attacker, Position attackPosition, UnitStack target) {
        if (attacker == null) throw new IllegalArgumentException("Attacker cannot be null");
        if (attackPosition == null) throw new IllegalArgumentException("Attack position cannot be null");
        if (target == null) throw new IllegalArgumentException("Target cannot be null");

        this.attacker = attacker;
        this.attackPosition = attackPosition;
        this.target = target;
        this.combatResolver = new CombatResolver();
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (!attacker.isAlive()) throw new IllegalStateException("Dead unit cannot attack");
        if (!target.isAlive()) throw new IllegalStateException("Cannot attack dead unit");
        if (state.getActiveUnit() != attacker) throw new IllegalStateException("Only active unit can attack");
        if (!state.getField().isInside(attackPosition)) throw new IllegalStateException("Attack position is outside the battlefield");

        if (!attackPosition.equals(attacker.getPosition()) &&
            (state.getPlayerOne().getArmy().isPositionOccupied(attackPosition)
                || state.getPlayerTwo().getArmy().isPositionOccupied(attackPosition))) return List.of();

        if (!BattlePathFinder.canReach(state, attacker, attackPosition)) return List.of();
        Position startPosition = attacker.getPosition();
        List<Position> path = BattlePathFinder.findPath(state, startPosition, attackPosition);

        List<BattleEvent> events = new ArrayList<>();
        events.add(new BattleEvent.UnitMoved(
            attacker,
            startPosition,
            attackPosition,
            path
        ));
        events.addAll(combatResolver.attack(state, attacker, target));
        attacker.changePosition(attackPosition);
        return events;
    }
}
