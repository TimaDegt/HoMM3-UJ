package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.unit.stack.ArcherUnitStack;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.ArrayList;
import java.util.List;

public class ShootAction implements BattleAction {
    private final ArcherUnitStack shooter;
    private final UnitStack target;

    public ShootAction(ArcherUnitStack shooter, UnitStack target) {
        if (shooter == null) throw new IllegalArgumentException("Shooter cannot be null");
        if (target == null) throw new IllegalArgumentException("Target cannot be null");

        this.shooter = shooter;
        this.target = target;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (!shooter.isAlive()) throw new IllegalStateException("Shooter is dead");
        if (state.getActiveUnit() != shooter) throw new IllegalStateException("Only active unit can shoot");
        if (!target.isAlive()) throw new IllegalStateException("Target is dead");
        if (shooter.getOwner() == target.getOwner()) throw new IllegalStateException("Cannot shoot allied unit");
        if (!shooter.canFire()) throw new IllegalStateException("Unit cannot shoot");

        shooter.fire();

        int damage = shooter.calculateDamageRoll();
        target.takeDamage(damage);

        List<BattleEvent> events = new ArrayList<>();
        events.add(new BattleEvent.UnitAttacked(UnitSnapshot.from(shooter), UnitSnapshot.from(target)));
        events.add(new BattleEvent.UnitDamaged(
            UnitSnapshot.from(target),
            damage,
            target.getCount(),
            target.getCurrentHp()
        ));

        if (!target.isAlive()) {
            events.add(new BattleEvent.UnitDied(UnitSnapshot.from(target), target.getPosition()));
        }
        return events;
    }

}
