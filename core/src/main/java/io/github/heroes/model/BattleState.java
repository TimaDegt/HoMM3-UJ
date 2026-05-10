package io.github.heroes.model;

public class BattleState {
    private final BattleField field;
    private final BattlePlayer playerOne;
    private final BattlePlayer playerTwo;

    private UnitStack activeUnit;
    private Player winner;
    private int round;

    public BattleState(BattleField field, BattlePlayer playerOne, BattlePlayer playerTwo) {
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
        this.round = 1;
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

    public boolean isFinished() {
        return winner != null;
    }
}
