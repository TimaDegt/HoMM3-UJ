package io.github.heroes.control;

import io.github.heroes.model.BotAI;
import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.combat.user.action.AffectPositionUserAction;
import io.github.heroes.model.combat.user.action.DefendUserAction;
import io.github.heroes.model.combat.user.action.OpenSpellBookUserAction;
import io.github.heroes.model.combat.user.action.UserAction;
import io.github.heroes.model.combat.user.action.WaitUserAction;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.unit.stack.UnitStack;

public class BattleController {
    private final BattleEngine battleEngine;
    private final BotAI botAI;

    public BattleController(BattleEngine battleEngine) {
        this.battleEngine = battleEngine;
        this.botAI = new BotAI();
    }

    public ActionResult onHexClicked(
        Position clickedPosition,
        Position nearestPosition
    ) {
        return performAction(new AffectPositionUserAction(clickedPosition, nearestPosition));
    }

    public ActionResult onDefendClicked() {
        return performAction(new DefendUserAction());
    }

    public ActionResult onWaitClicked() {
        return performAction(new WaitUserAction());
    }

    public ActionResult onSpellBookClicked() {
        return performAction(new OpenSpellBookUserAction());
    }


    public ActionResult performAction(UserAction action) {
        return battleEngine.performAction(action);
    }

    public BattleState getState() {
        return battleEngine.getState();
    }

    public ActionResult onReadyForNextAction() {
        if (!battleEngine.getState().isCurrentPlayerBot(battleEngine.getActiveUnit())) {
            return ActionResult.failure();
        }

        UserAction action = botAI.takeTurn(battleEngine.getState());
        if (action == null) return ActionResult.failure();

        return performAction(action);
    }

    public BattleField getField() {
        return battleEngine.getState().getField();
    }

    public UnitStack findUnitAt(Position position) {
        return battleEngine.findUnitAt(position);
    }

}
