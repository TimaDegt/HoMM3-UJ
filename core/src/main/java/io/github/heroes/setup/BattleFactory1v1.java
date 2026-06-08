package io.github.heroes.setup;

<<<<<<< HEAD:core/src/main/java/io/github/heroes/setup/BattleFactory.java
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.state.Army;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.model.state.BattlePlayer;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;
import io.github.heroes.model.state.UnitType;
=======
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
>>>>>>> origin/lobby2:core/src/main/java/io/github/heroes/setup/BattleFactory1v1.java

public class BattleFactory1v1 {
    private BattleFactory1v1() {
    }

    public static BattleEngine createDemoBattle() {
        BattleField field = new BattleField(15, 11);

        Army playerOneArmy = new Army();
        playerOneArmy.addUnit(new UnitStack(UnitType.PIKEMAN, 2, new Position(1, 5), Player.PLAYER_ONE));
        playerOneArmy.addUnit(new UnitStack(UnitType.ARCHER, 1, new Position(1, 3), Player.PLAYER_ONE));

        Army playerTwoArmy = new Army();
        playerTwoArmy.addUnit(new UnitStack(UnitType.GRIFFIN, 2, new Position(13, 5), Player.PLAYER_TWO));
        playerTwoArmy.addUnit(new UnitStack(UnitType.ARCHER, 2, new Position(13, 3), Player.PLAYER_TWO));

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

<<<<<<< HEAD:core/src/main/java/io/github/heroes/setup/BattleFactory.java
        BattleState state = new BattleState(field, playerOne, playerTwo);
        return new BattleEngine(state);
=======
        BattleState state = new BattleState(field, playerOne, playerTwo, false);
        return new BattleController(state);
>>>>>>> origin/lobby2:core/src/main/java/io/github/heroes/setup/BattleFactory1v1.java
    }
}
