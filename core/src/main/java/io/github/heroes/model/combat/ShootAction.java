package io.github.heroes.model.combat;

import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.UnitStack;

import java.util.ArrayList;
import java.util.List;

public class ShootAction implements BattleAction {
    private final UnitStack shooter;
    private final UnitStack target;

    public ShootAction(UnitStack shooter, UnitStack target) {
        if (shooter == null) throw new IllegalArgumentException("Shooter cannot be null");
        if (target == null) throw new IllegalArgumentException("Target cannot be null");

        this.shooter = shooter;
        this.target = target;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (!shooter.isAlive()) throw new IllegalStateException("Shooter is dead");
        if (!target.isAlive()) throw new IllegalStateException("Target is dead");
        if (shooter.getOwner() == target.getOwner()) throw new IllegalStateException("Cannot shoot allied unit");
        if (!shooter.canFire()) throw new IllegalStateException("Unit cannot shoot");

        shooter.fire();

        int damage = shooter.calculateDamageRoll();
        target.takeDamage(damage);

        List<BattleEvent> events = new ArrayList<>();
        events.add(new BattleEvent.UnitAttacked(shooter, target));
        events.add(new BattleEvent.UnitDamaged(
            target,
            damage,
            target.getCount(),
            target.getCurrentHp()
        ));

        if (!target.isAlive()) {
            events.add(new BattleEvent.UnitDied(target, target.getPosition()));
        }

        updateWinner(state);
        return events;
    }

    private void updateWinner(BattleState state) {
        if (state.getPlayerOne().isDefeated()) {
            state.setWinner(Player.PLAYER_TWO);
        } else if (state.getPlayerTwo().isDefeated()) {
            state.setWinner(Player.PLAYER_ONE);
        }
    }
}
