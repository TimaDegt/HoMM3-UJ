package io.github.heroes.model.state;

import io.github.heroes.model.magic.Spell;
import io.github.heroes.model.state.unit.stack.UnitStack;

public class BattleState {
    private final BattleField field;
    private final BattlePlayer playerOne;
    private final BattlePlayer playerTwo;
    private final boolean isBot;

    private UnitStack activeUnit;
    private Player winner;
    private int round;
    private boolean magicMode;
    private Spell chosenSpell;

    public BattleState(BattleField field, BattlePlayer playerOne, BattlePlayer playerTwo, boolean isBot) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        if (playerOne == null) {
            throw new IllegalArgumentException("Player one cannot be null");
        }
        if (playerTwo == null) {
            throw new IllegalArgumentException("Player two cannot be null");
        }

        this.field = field;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.isBot = isBot;
        this.round = 1;
        this.magicMode = false;
        this.chosenSpell = null;
    }

    public boolean isCurrentPlayerBot(UnitStack activeUnit) {
        if (activeUnit == null) return false;
        return this.isBot && activeUnit.getOwner() == Player.PLAYER_TWO;
    }

    public BattleField getField() {
        return field;
    }

    public BattlePlayer getPlayerOne() {
        return playerOne;
    }

    public BattlePlayer getPlayerTwo() {
        return playerTwo;
    }

    public UnitStack getActiveUnit() {
        return activeUnit;
    }

    public void setActiveUnit(UnitStack activeUnit) {
        this.activeUnit = activeUnit;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public int getRound() {
        return round;
    }

    public void nextRound() {
        round++;
    }

    public boolean isMagicMode() {
        return magicMode;
    }

    public Spell getChosenSpell() {
        return chosenSpell;
    }

    public void selectSpell(Spell spell) {
        if (spell == null) throw new IllegalArgumentException("Spell cannot be null");
        chosenSpell = spell;
        magicMode = true;
    }

    public void clearMagicMode() {
        chosenSpell = null;
        magicMode = false;
    }

    public boolean isFinished() {
        return winner != null;
    }
}
