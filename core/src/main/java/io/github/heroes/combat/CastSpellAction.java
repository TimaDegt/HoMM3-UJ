package io.github.heroes.combat;

import io.github.heroes.magic.Spell;
import io.github.heroes.model.BattleState;
import io.github.heroes.model.Hero;
import io.github.heroes.model.Player;
import io.github.heroes.model.UnitStack;

public class CastSpellAction implements BattleAction {
    private final Hero caster;
    private final Spell spell;
    private final UnitStack target;

    public CastSpellAction(Hero caster, Spell spell, UnitStack target) {
        if (caster == null) {
            throw new IllegalArgumentException("Caster cannot be null");
        }
        if (spell == null) {
            throw new IllegalArgumentException("Spell cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }

        this.caster = caster;
        this.spell = spell;
        this.target = target;
    }

    @Override
    public void execute(BattleState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        if (!target.isAlive()) {
            throw new IllegalStateException("Cannot cast spell on dead unit");
        }

        spell.cast(caster, target);
        updateWinner(state);
    }

    private void updateWinner(BattleState state) {
        if (state.getPlayerOne().isDefeated()) {
            state.setWinner(Player.PLAYER_TWO);
        } else if (state.getPlayerTwo().isDefeated()) {
            state.setWinner(Player.PLAYER_ONE);
        }
    }
}
