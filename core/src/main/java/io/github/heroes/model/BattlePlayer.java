package io.github.heroes.model;

public class BattlePlayer {
    private final Player side;
    private final Hero hero;
    private final Army army;

    public BattlePlayer(Player side, Hero hero, Army army) {
        if (side == null) {
            throw new IllegalArgumentException("Side cannot be null");
        }
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null");
        }
        if (army == null) {
            throw new IllegalArgumentException("Army cannot be null");
        }

        this.side = side;
        this.hero = hero;
        this.army = army;
    }

    public Player getSide() {
        return side;
    }

    public Hero getHero() {
        return hero;
    }

    public Army getArmy() {
        return army;
    }

    public boolean isDefeated() {
        return army.isDefeated();
    }
}
