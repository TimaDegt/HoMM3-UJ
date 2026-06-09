package io.github.heroes.control;

import io.github.heroes.model.BotAI;
import io.github.heroes.model.combat.*;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;

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
        if (battleEngine.getState().isFinished()) return ActionResult.failure();

        UnitStack clickedUnit = battleEngine.findUnitAt(clickedPosition);
        UnitStack attackUnit = battleEngine.getActiveUnit();
        if (clickedUnit != null) {
            if (clickedUnit.getOwner()==attackUnit.getOwner() || nearestPosition == null){
                return ActionResult.failure();
            }
            return performAction(new MoveAndAttackAction(attackUnit,nearestPosition,clickedUnit));
        }

        return performAction(new MoveAction(attackUnit, clickedPosition));
    }

    public ActionResult onDefendClicked() {
        UnitStack activeUnit = battleEngine.getActiveUnit();
        if (activeUnit == null) return ActionResult.failure();

        return performAction(new DefendAction(activeUnit));
    }

    public ActionResult onWaitClicked() {
        UnitStack activeUnit = battleEngine.getActiveUnit();
        if (activeUnit == null) return ActionResult.failure();

        return performAction(new WaitAction(activeUnit));
    }

    public ActionResult onSpellBookClicked() {
        return performAction(new NoAction());
    }


    public ActionResult performAction(BattleAction action) {
        return battleEngine.performAction(action);
    }

    public BattleState getState() {
        return battleEngine.getState();
    }

    public UnitStack getActiveUnit() {
        return battleEngine.getActiveUnit();
    }

    public ActionResult onReadyForNextAction() {
        UnitStack activeUnit = battleEngine.getActiveUnit();
        if (!battleEngine.getState().isCurrentPlayerBot(activeUnit)) return ActionResult.failure();

        BattleAction action = botAI.takeTurn(battleEngine.getState());
        if (action == null) return ActionResult.failure();

        return performAction(action);
    }

    public BattleField getField() {
        return battleEngine.getState().getField();
    }

    public UnitStack findUnitAt(Position position) {
        return battleEngine.findUnitAt(position);
    }

    private boolean canReach(UnitStack unit, Position targetPosition) {
        return BattlePathFinder.canReach(battleEngine.getState(), unit, targetPosition);
    }
}
