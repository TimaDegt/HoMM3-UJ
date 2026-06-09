package io.github.heroes.model.combat.unit.action;

import io.github.heroes.model.magic.Spell;
import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.ArrayList;
import java.util.List;

public class CastSpellAction implements BattleAction {
    private final Hero caster;
    private final Spell spell;
    private final UnitStack target;

    public CastSpellAction(Hero caster, Spell spell, UnitStack target) {
        if (caster == null) throw new IllegalArgumentException("Caster cannot be null");
        if (spell == null) throw new IllegalArgumentException("Spell cannot be null");
        if (target == null) throw new IllegalArgumentException("Target cannot be null");

        this.caster = caster;
        this.spell = spell;
        this.target = target;
    }

    @Override
    public List<BattleEvent> execute(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");
        if (!target.isAlive()) throw new IllegalStateException("Cannot cast spell on dead unit");
        if (!spell.canCast(caster)) throw new IllegalStateException("Not enough mana to cast spell");

        int hpBefore = totalHp(target);
        spell.cast(caster, target);
        int damage = hpBefore - totalHp(target);

        List<BattleEvent> events = new ArrayList<>();
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

    private int totalHp(UnitStack unit) {
        if (!unit.isAlive()) return 0;
        return (unit.getCount() - 1) * unit.getMaxHp() + unit.getCurrentHp();
    }
}
