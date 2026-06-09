package io.github.heroes.model.combat;

import com.badlogic.gdx.Gdx;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.UnitStack;

import java.util.ArrayList;
import java.util.List;

public class CombatResolver {

    public List<BattleEvent> attack(BattleState state, UnitStack attacker, UnitStack target) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (attacker == null) throw new IllegalArgumentException("Attacker cannot be null");
        if (target == null) throw new IllegalArgumentException("Target cannot be null");
        if (!attacker.isAlive()) throw new IllegalStateException("Attacker is dead");
        if (!target.isAlive()) throw new IllegalStateException("Target is dead");
        if (attacker.getOwner() == target.getOwner()) throw new IllegalStateException("Cannot attack allied unit");

        int damage = calculateDamage(state, attacker, target);
        target.takeDamage(damage);

        List<BattleEvent> events = new ArrayList<>();
        events.add(new BattleEvent.UnitAttacked(attacker, target));
        events.add(new BattleEvent.UnitDamaged(
            target,
            damage,
            target.getCount(),
            target.getCurrentHp()
        ));
        if (!target.isAlive()) {
            events.add(new BattleEvent.UnitDied(target, target.getPosition()));
        }

        return events;
    }

    private int calculateDamage(BattleState state, UnitStack attacker, UnitStack target) {
        int attackerAttack = attacker.getAttack()
            + getHeroAttack(state, attacker.getOwner());

        int targetDefense = target.getDefense()
            + getHeroDefense(state, target.getOwner());

        if (target.isDefending()) {
            targetDefense = (int) Math.round(targetDefense * 1.2);
        }
        int baseDamage = attacker.calculateDamageRoll();

        int difference = attackerAttack - targetDefense;
        double multiplier;

        if (difference >= 0) {
            multiplier = 1.0 + difference * 0.05;
        } else {
            multiplier = 1.0 + difference * 0.025;
        }
        multiplier = Math.max(0.3, multiplier);
        multiplier = Math.min(3.0, multiplier);

        return (int) Math.round(baseDamage * multiplier);
    }

    private int getHeroAttack(BattleState state, Player owner) {
        if (owner == Player.PLAYER_ONE) return state.getPlayerOne().getHero().getAttack();
        return state.getPlayerTwo().getHero().getAttack();
    }

    private int getHeroDefense(BattleState state, Player owner) {
        if (owner == Player.PLAYER_ONE) return state.getPlayerOne().getHero().getDefense();
        return state.getPlayerTwo().getHero().getDefense();
    }
}
