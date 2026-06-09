package io.github.heroes.control;

import io.github.heroes.model.BotAI;
import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.combat.BattleEngine;
import io.github.heroes.model.combat.user.action.AffectPositionUserAction;
import io.github.heroes.model.combat.user.action.CastSpellUserAction;
import io.github.heroes.model.combat.user.action.DefendUserAction;
import io.github.heroes.model.combat.user.action.OpenSpellBookUserAction;
import io.github.heroes.model.combat.user.action.UserAction;
import io.github.heroes.model.combat.user.action.WaitUserAction;
import io.github.heroes.model.magic.Spell;
import io.github.heroes.model.snapshot.BattleSnapshot;
import io.github.heroes.model.snapshot.TurnQueueEntrySnapshot;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.Position;

import java.util.List;
import java.util.Set;

public class BattleController {
    private final BattleEngine battleEngine;
    private final BotAI botAI;

    public BattleController(BattleEngine battleEngine) {
        if (battleEngine == null) throw new IllegalArgumentException("Battle engine cannot be null");
        this.battleEngine = battleEngine;
        this.botAI = new BotAI();
    }

    public ActionResult onHexClicked(
        Position clickedPosition,
        Position attackFromPosition
    ) {
        return performAction(new AffectPositionUserAction(clickedPosition, attackFromPosition));
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

    public ActionResult onSpellCast(Spell spell, Position targetPosition) {
        return performAction(new CastSpellUserAction(spell, targetPosition));
    }

    public ActionResult performAction(UserAction action) {
        return battleEngine.performAction(action);
    }

    public ActionResult onReadyForNextAction() {
        if (!battleEngine.getState().isCurrentPlayerBot(battleEngine.getActiveUnit())) {
            return ActionResult.failure();
        }

        UserAction action = botAI.takeTurn(battleEngine.getState());
        if (action == null) return ActionResult.failure();

        return performAction(action);
    }

    public BattleSnapshot getBattleSnapshot() {
        return battleEngine.getSnapshot();
    }

    public List<TurnQueueEntrySnapshot> getTurnQueue() {
        return battleEngine.getTurnQueueOrder();
    }

    public Set<Position> getReachablePositions() {
        return battleEngine.getReachablePositions();
    }

    public boolean canActiveUnitReach(Position position) {
        return battleEngine.canActiveUnitReach(position);
    }

    public boolean isPositionOccupied(Position position) {
        return battleEngine.isPositionOccupied(position);
    }

    public UnitSnapshot findUnitAt(Position position) {
        return getBattleSnapshot().findUnitAt(position);
    }
}
