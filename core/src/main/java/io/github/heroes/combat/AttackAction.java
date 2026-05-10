package io.github.heroes.combat;

import io.github.heroes.model.BattleState;
import io.github.heroes.model.UnitStack;

public class AttackAction implements BattleAction {
    private final UnitStack attacker;
    private final UnitStack target;
    private final CombatResolver combatResolver;

    public AttackAction(UnitStack attacker, UnitStack target) {
        if (attacker == null) {
            throw new IllegalArgumentException("Attacker cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }

        this.attacker = attacker;
        this.target = target;
        this.combatResolver = new CombatResolver();
    }

    @Override
    public void execute(BattleState state) {
        combatResolver.attack(state, attacker, target);
    }
}
