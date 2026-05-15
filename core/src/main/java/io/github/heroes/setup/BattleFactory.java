package io.github.heroes.setup;

import io.github.heroes.combat.BattleController;
import io.github.heroes.model.Army;
import io.github.heroes.model.BattleField;
import io.github.heroes.model.BattlePlayer;
import io.github.heroes.model.BattleState;
import io.github.heroes.model.Hero;
import io.github.heroes.model.Player;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.model.UnitType;

public class BattleFactory {
    private BattleFactory() {
    }

    public static BattleController createDemoBattle() {
        BattleField field = new BattleField(15, 11);

        Army playerOneArmy = new Army();
        playerOneArmy.addUnit(new UnitStack(UnitType.PIKEMAN, 20, new Position(1, 5), Player.PLAYER_ONE));
        playerOneArmy.addUnit(new UnitStack(UnitType.ARCHER, 10, new Position(1, 3), Player.PLAYER_ONE));

        Army playerTwoArmy = new Army();
        playerTwoArmy.addUnit(new UnitStack(UnitType.GRIFFIN, 8, new Position(13, 5), Player.PLAYER_TWO));
        playerTwoArmy.addUnit(new UnitStack(UnitType.ARCHER, 12, new Position(13, 3), Player.PLAYER_TWO));

        BattlePlayer playerOne = new BattlePlayer(
            Player.PLAYER_ONE,
            new Hero("Knight", 2, 2, 1, 1),
            playerOneArmy
        );

        BattlePlayer playerTwo = new BattlePlayer(
            Player.PLAYER_TWO,
            new Hero("Warlock", 2, 2, 1, 1),
            playerTwoArmy
        );

        BattleState state = new BattleState(field, playerOne, playerTwo);
        return new BattleController(state);
    }
}
