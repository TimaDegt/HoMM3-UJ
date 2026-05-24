package io.github.heroes.combat;

import io.github.heroes.model.BattleState;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;

import java.util.List;

public class MoveAndAttackAction implements BattleAction {
    private final UnitStack attacker;
    private final Position attackPosition;
    private final UnitStack target;
    private final CombatResolver combatResolver;

    public MoveAndAttackAction(UnitStack attacker, Position attackPosition, UnitStack target) {
        if (attacker == null) {
            throw new IllegalArgumentException("Attacker cannot be null");
        }
        if (attackPosition == null) {
            throw new IllegalArgumentException("Attack position cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }

        this.attacker = attacker;
        this.attackPosition = attackPosition;
        this.target = target;
        this.combatResolver = new CombatResolver();
    }

    @Override
    public void execute(BattleState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        if (!attacker.isAlive()) {
            throw new IllegalStateException("Dead unit cannot attack");
        }
        if (!target.isAlive()) {
            throw new IllegalStateException("Cannot attack dead unit");
        }
        if (state.getActiveUnit() != attacker) {
            throw new IllegalStateException("Only active unit can attack");
        }
        if (!state.getField().isInside(attackPosition)) {
            throw new IllegalStateException("Attack position is outside the battlefield");
        }

        List<Position> path = BattlePathFinder.findPath(state,attacker.getPosition(),attackPosition);
        attacker.initiateMovement(path);
        combatResolver.attack(state, attacker, target);
    }
}
