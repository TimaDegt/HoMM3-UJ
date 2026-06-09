package io.github.heroes.model.combat;

import io.github.heroes.magic.Spell;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.UnitStack;

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

        int hpBefore = totalHp(target);
        spell.cast(caster, target);
        int damage = hpBefore - totalHp(target);

        List<BattleEvent> events = new ArrayList<>();
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

    private int totalHp(UnitStack unit) {
        if (!unit.isAlive()) return 0;
        return (unit.getCount() - 1) * unit.getMaxHp() + unit.getCurrentHp();
    }

    private void updateWinner(BattleState state) {
        if (state.getPlayerOne().isDefeated()) {
            state.setWinner(Player.PLAYER_TWO);
        } else if (state.getPlayerTwo().isDefeated()) {
            state.setWinner(Player.PLAYER_ONE);
        }
    }
}
