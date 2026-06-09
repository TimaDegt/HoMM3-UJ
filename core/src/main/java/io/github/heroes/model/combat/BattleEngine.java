package io.github.heroes.model.combat;

import io.github.heroes.model.combat.unit.action.*;
import io.github.heroes.model.combat.user.action.AffectPositionUserAction;
import io.github.heroes.model.combat.user.action.CastSpellUserAction;
import io.github.heroes.model.combat.user.action.DefendUserAction;
import io.github.heroes.model.combat.user.action.NoOpUserAction;
import io.github.heroes.model.combat.user.action.OpenSpellBookUserAction;
import io.github.heroes.model.combat.user.action.UserAction;
import io.github.heroes.model.combat.user.action.WaitUserAction;
import io.github.heroes.model.snapshot.BattleSnapshot;
import io.github.heroes.model.snapshot.TurnQueueEntrySnapshot;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BattleEngine {
    private final BattleState state;
    private final TurnQueue turnQueue;

    public BattleEngine(BattleState state) {
        if (state == null) throw new IllegalArgumentException("State cannot be null");

        this.state = state;
        this.turnQueue = new TurnQueue(state);
    }

    public ActionResult performAction(UserAction action) {
        if (action == null) return ActionResult.failure();
        if (state.isFinished()) return ActionResult.failure();

        BattleAction battleAction = resolveAction(action);
        if (battleAction == null) return ActionResult.failure();

        List<BattleEvent> events;
        try {
            events = new ArrayList<>(battleAction.execute(state));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ActionResult.failure();
        }

        if (events.isEmpty())return ActionResult.failure();

        updateWinner();

        if (state.isFinished()) {
            events.add(new BattleEvent.BattleFinished(state.getWinner()));
        } else if (battleAction.endsTurn()) {
            if (battleAction instanceof WaitAction) turnQueue.waitCurrentUnit();
            else turnQueue.nextTurn();
        }

        return ActionResult.success(events);
    }

    public BattleState getState() {
        return state;
    }

    public UnitStack getActiveUnit() {
        return state.getActiveUnit();
    }

    public List<TurnQueueEntrySnapshot> getTurnQueueOrder() {
        return turnQueue.getQueue().stream()
            .map(TurnQueueEntrySnapshot::from)
            .toList();
    }

    public BattleSnapshot getSnapshot() {
        return BattleSnapshot.from(state);
    }

    public boolean isPositionOccupied(Position position) {
        return state.getPlayerOne().getArmy().isPositionOccupied(position)
            || state.getPlayerTwo().getArmy().isPositionOccupied(position);
    }

    public UnitStack findUnitAt(Position position) {
        if (position == null) return null;
        UnitStack unit = state.getPlayerOne().getArmy().findUnitAtPosition(position);
        if (unit != null) return unit;

        return state.getPlayerTwo().getArmy().findUnitAtPosition(position);
    }

    public Set<Position> getReachablePositions() {
        return BattlePathFinder.findReachablePositions(state, getActiveUnit());
    }

    public BattleActionPreview previewAction(
        Position targetPosition,
        Position attackFromPosition
    ) {
        UnitStack activeUnit = getActiveUnit();
        if (state.isFinished()
            || activeUnit == null
            || !activeUnit.isAlive()
            || !state.getField().isInside(targetPosition)) {
            return BattleActionPreview.INVALID;
        }

        UnitStack target = findUnitAt(targetPosition);
        if (target == null) {
            return BattlePathFinder.canReach(state, activeUnit, targetPosition)
                ? BattleActionPreview.MOVE
                : BattleActionPreview.INVALID;
        }
        if (target.getOwner() == activeUnit.getOwner()) {
            return BattleActionPreview.ALLY;
        }
        if (activeUnit.canAttackWithoutMoving(target)) {
            return BattleActionPreview.RANGED_ATTACK;
        }
        if (canMeleeAttackFrom(activeUnit, target, attackFromPosition)) {
            return BattleActionPreview.MELEE_ATTACK;
        }
        if (activeUnit.supportsRangedAttack()) {
            return BattleActionPreview.RANGED_ATTACK_UNAVAILABLE;
        }
        return BattleActionPreview.INVALID;
    }

    private boolean canMeleeAttackFrom(
        UnitStack activeUnit,
        UnitStack target,
        Position attackFromPosition
    ) {
        if (!state.getField().isInside(attackFromPosition)) return false;
        if (!isAdjacent(attackFromPosition, target.getPosition())) return false;
        if (!attackFromPosition.equals(activeUnit.getPosition())
            && isPositionOccupied(attackFromPosition)) return false;
        return BattlePathFinder.canReach(state, activeUnit, attackFromPosition);
    }

    private boolean isAdjacent(Position first, Position second) {
        for (Position neighbor : second.neighbors()) {
            if (neighbor.equals(first)) return true;
        }
        return false;
    }

    private BattleAction resolveAction(UserAction action) {
        UnitStack activeUnit = getActiveUnit();
        if (activeUnit == null) return null;

        if (action instanceof AffectPositionUserAction affectPositionAction) {
            return resolvePositionAction(activeUnit, affectPositionAction);
        }
        if (action instanceof DefendUserAction) {
            return new DefendAction(activeUnit);
        }
        if (action instanceof WaitUserAction) {
            return new WaitAction(activeUnit);
        }
        if (action instanceof CastSpellUserAction castSpellAction) {
            UnitStack target = findUnitAt(castSpellAction.getTargetPosition());
            if (target == null) return null;
            return new CastSpellAction(
                getActiveHero(activeUnit),
                castSpellAction.getSpell(),
                target
            );
        }
        if (action instanceof OpenSpellBookUserAction) {
            return new OpenSpellBookAction(getActiveHero(activeUnit).getSpellBook());
        }
        if (action instanceof NoOpUserAction) {
            return new NoAction();
        }
        return null;
    }

    private BattleAction resolvePositionAction(
        UnitStack activeUnit,
        AffectPositionUserAction action
    ) {
        Position affectedPosition = action.getAffectedPosition();
        UnitStack target = findUnitAt(affectedPosition);

        if (target == null) return new MoveAction(activeUnit, affectedPosition);
        if (target.getOwner() == activeUnit.getOwner()) return null;
        return activeUnit.createAttackAction(target, action.getNearestPosition());
    }

    private Hero getActiveHero(UnitStack activeUnit) {
        if (activeUnit.getOwner() == Player.PLAYER_ONE) {
            return state.getPlayerOne().getHero();
        }
        return state.getPlayerTwo().getHero();
    }

    private void updateWinner() {
        if (state.getPlayerOne().isDefeated()) {
            state.setWinner(Player.PLAYER_TWO);
        } else if (state.getPlayerTwo().isDefeated()) {
            state.setWinner(Player.PLAYER_ONE);
        }
    }
}
