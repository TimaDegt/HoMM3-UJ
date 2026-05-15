package io.github.heroes.combat;

import io.github.heroes.model.BattleState;
import io.github.heroes.model.Player;
import io.github.heroes.model.UnitStack;

public class CombatResolver {

    public void attack(BattleState state, UnitStack attacker, UnitStack target) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        if (attacker == null) {
            throw new IllegalArgumentException("Attacker cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        if (!attacker.isAlive()) {
            throw new IllegalStateException("Attacker is dead");
        }
        if (!target.isAlive()) {
            throw new IllegalStateException("Target is dead");
        }
        if (attacker.getOwner() == target.getOwner()) {
            throw new IllegalStateException("Cannot attack allied unit");
        }

        int damage = calculateDamage(state, attacker, target);
        target.takeDamage(damage);

        updateWinner(state);
    }

    private int calculateDamage(BattleState state, UnitStack attacker, UnitStack target) {
        int attackerAttack = attacker.getType().attack + getHeroAttack(state, attacker.getOwner());
        int targetDefense = target.getType().defense + getHeroDefense(state, target.getOwner());
        if (target.isDefending()) {
            targetDefense = (int) Math.round(targetDefense * 1.2);
        }

        int baseDamage = attacker.getType().damage * attacker.getCount();

        int difference = attackerAttack - targetDefense;

        double multiplier;

        if (difference >= 0) {
            multiplier = 1.0 + difference * 0.05;
        } else {
            multiplier = 1.0 + difference * 0.025;
        }

        multiplier = Math.max(0.3, multiplier);
        multiplier = Math.min(3, multiplier);

        return (int) Math.round(baseDamage * multiplier);
    }

    private int getHeroAttack(BattleState state, Player owner) {
        if (owner == Player.PLAYER_ONE) {
            return state.getPlayerOne().getHero().getAttack();
        }

        return state.getPlayerTwo().getHero().getAttack();
    }

    private int getHeroDefense(BattleState state, Player owner) {
        if (owner == Player.PLAYER_ONE) {
            return state.getPlayerOne().getHero().getDefense();
        }

        return state.getPlayerTwo().getHero().getDefense();
    }

    private void updateWinner(BattleState state) {
        if (state.getPlayerOne().isDefeated()) {
            state.setWinner(Player.PLAYER_TWO);
        } else if (state.getPlayerTwo().isDefeated()) {
            state.setWinner(Player.PLAYER_ONE);
        }
    }
}
