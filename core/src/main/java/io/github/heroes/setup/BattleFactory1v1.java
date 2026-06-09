package io.github.heroes.setup;

import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.spellBook.SpellBook;
import io.github.heroes.model.state.Army;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.model.state.BattlePlayer;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;
import io.github.heroes.model.state.UnitType;

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

        BattleState state = new BattleState(field, playerOne, playerTwo, false);
        return new BattleEngine(state);
    }
}
