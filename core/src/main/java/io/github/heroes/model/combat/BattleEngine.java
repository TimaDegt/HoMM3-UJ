package io.github.heroes.model.combat;

import io.github.heroes.model.combat.unit.action.*;
import io.github.heroes.model.combat.user.action.AffectPositionAction;
import io.github.heroes.model.combat.user.action.UserAction;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;

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

    public List<TurnQueueEntry> getTurnQueueOrder() {
        return turnQueue.getQueue();
    }

    public boolean isPositionOccupied(Position position) {
        return state.getPlayerOne().getArmy().isPositionOccupied(position)
            || state.getPlayerTwo().getArmy().isPositionOccupied(position);
    }

    public UnitStack findUnitAt(Position position) {
        UnitStack unit = state.getPlayerOne().getArmy().findUnitAtPosition(position);
        if (unit != null) return unit;

        return state.getPlayerTwo().getArmy().findUnitAtPosition(position);
    }

    public Set<Position> getReachablePositions() {
        return BattlePathFinder.findReachablePositions(state, getActiveUnit());
    }

    private BattleAction resolveAction(UserAction action) {
        UnitStack activeUnit = getActiveUnit();
        if (activeUnit == null) return null;

        if (action instanceof AffectPositionAction affectPositionAction) {
            return resolvePositionAction(activeUnit, affectPositionAction);
        }
        if (action instanceof io.github.heroes.model.combat.user.action.DefendAction) {
            return new DefendAction(activeUnit);
        }
        if (action instanceof io.github.heroes.model.combat.user.action.WaitAction) {
            return new WaitAction(activeUnit);
        }
        if (action instanceof io.github.heroes.model.combat.user.action.CastSpellAction castSpellAction) {
            return new CastSpellAction(
                getActiveHero(activeUnit),
                castSpellAction.getSpell(),
                castSpellAction.getTarget()
            );
        }
        if (action instanceof io.github.heroes.model.combat.user.action.OpenSpellBookAction) {
            return new OpenSpellBookAction(getActiveHero(activeUnit).getSpellBook());
        }
        if (action instanceof io.github.heroes.model.combat.user.action.NoAction) {
            return new NoAction();
        }
        return null;
    }

    private BattleAction resolvePositionAction(
        UnitStack activeUnit,
        AffectPositionAction action
    ) {
        Position affectedPosition = action.getAffectedPosition();
        UnitStack target = findUnitAt(affectedPosition);

        if (target == null) {
            return new MoveAction(activeUnit, affectedPosition);
        }
        if (target.getOwner() == activeUnit.getOwner()) {
            return null;
        }
        if (activeUnit.canFire()) {
            return new ShootAction(activeUnit, target);
        }

        Position attackPosition = action.getNearestPosition();
        if (attackPosition == null) return null;
        return new MoveAndAttackAction(activeUnit, attackPosition, target);
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
