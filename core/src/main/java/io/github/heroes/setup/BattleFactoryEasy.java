package io.github.heroes.setup;

import io.github.heroes.control.BattleController;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.magic.Spells;
import io.github.heroes.model.state.unit.stack.ArcherUnitStack;
import io.github.heroes.model.state.Army;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.model.state.BattlePlayer;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.unit.stack.DefaultUnitStack;
import io.github.heroes.model.state.UnitType;

public class BattleFactoryEasy {
    private BattleFactoryEasy() {
    }

    public static BattleController createDemoController() {
        return new BattleController(createDemoBattle());
    }
    public static BattleEngine createDemoBattle() {
        BattleField field = new BattleField(15, 11);

        Army playerOneArmy = new Army();
        playerOneArmy.addUnit(new DefaultUnitStack(UnitType.PIKEMAN, 5, new Position(1, 5), Player.PLAYER_ONE));
        playerOneArmy.addUnit(new ArcherUnitStack(2, new Position(1, 3), Player.PLAYER_ONE));
        playerOneArmy.addUnit(new ArcherUnitStack(2, new Position(1, 7), Player.PLAYER_ONE));
        playerOneArmy.addUnit(new DefaultUnitStack(UnitType.CAVALIER, 2, new Position(1, 1), Player.PLAYER_ONE));
        playerOneArmy.addUnit(new DefaultUnitStack(UnitType.CAVALIER, 2, new Position(1, 9), Player.PLAYER_ONE));

        Army playerTwoArmy = new Army();
        playerTwoArmy.addUnit(new DefaultUnitStack(UnitType.GRIFFIN, 2, new Position(13, 5), Player.PLAYER_TWO));
        playerTwoArmy.addUnit(new ArcherUnitStack(2, new Position(13, 3), Player.PLAYER_TWO));
        playerTwoArmy.addUnit(new DefaultUnitStack(UnitType.GRIFFIN, 2, new Position(13, 7), Player.PLAYER_TWO));
        playerTwoArmy.addUnit(new DefaultUnitStack(UnitType.ANGEL, 1, new Position(13, 9), Player.PLAYER_TWO));

        Hero playerOneHero = new Hero("Knight", 2, 2, 1, 5);
        playerOneHero.getSpellBook().learnSpell(Spells.MAGIC_ARROW.getSpell());
        playerOneHero.getSpellBook().learnSpell(Spells.HASTE.getSpell());
        playerOneHero.getSpellBook().learnSpell(Spells.BLESS.getSpell());

        Hero playerTwoHero = new Hero("Warlock", 2, 2, 1, 5);
        playerTwoHero.getSpellBook().learnSpell(Spells.MAGIC_ARROW.getSpell());
        playerTwoHero.getSpellBook().learnSpell(Spells.SLOW.getSpell());
        playerTwoHero.getSpellBook().learnSpell(Spells.CURSE.getSpell());

        BattlePlayer playerOne = new BattlePlayer(
            Player.PLAYER_ONE,
            playerOneHero,
            playerOneArmy
        );

        BattlePlayer playerTwo = new BattlePlayer(
            Player.PLAYER_TWO,
            playerTwoHero,
            playerTwoArmy
        );

        BattleState state = new BattleState(field, playerOne, playerTwo, true);
        return new BattleEngine(state);
    }
}
