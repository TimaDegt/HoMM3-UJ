package io.github.heroes.model.state.unit.stack;

import io.github.heroes.model.combat.unit.action.BattleAction;
import io.github.heroes.model.combat.unit.action.ShootAction;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitType;

public class ArcherUnitStack extends UnitStack {
    private static final int INITIAL_AMMO = 12;
    private int ammo;

    public ArcherUnitStack(int count, Position position, Player owner) {
        super(UnitType.ARCHER, count, position, owner);
        this.ammo = INITIAL_AMMO;
    }

    @Override
    public BattleAction createAttackAction(UnitStack target, Position attackPosition) {
        if (target == null) throw new IllegalArgumentException("Target cannot be null");
        if (canFire()) return new ShootAction(this, target);
        return super.createAttackAction(target, attackPosition);
    }

    @Override
    public boolean supportsRangedAttack() {
        return true;
    }

    @Override
    public boolean canAttackWithoutMoving(UnitStack target) {
        return target != null && canFire();
    }

    public int getAmmo() {
        return ammo;
    }

    public boolean canFire() {
        return ammo > 0;
    }

    public void fire() {
        if (ammo > 0) ammo--;
    }
}
