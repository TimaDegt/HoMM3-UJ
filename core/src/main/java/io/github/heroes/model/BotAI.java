package io.github.heroes.model;

import io.github.heroes.model.combat.*;
import io.github.heroes.model.state.Army;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;

public class BotAI {
    public BattleAction takeTurn(BattleState state) {
        UnitStack activeUnit = state.getActiveUnit();
        if (activeUnit == null || !activeUnit.isAlive()) return null;
        UnitStack target = findNearestEnemy(state, activeUnit);
        if (target == null) return null;

        Position attackPos = null;

        for (int row = 0; row < state.getField().getHeight(); row++) {
            for (int col = 0; col < state.getField().getWidth(); col++) {
                Position p = new Position(col, row);

                if (BattlePathFinder.canReach(state, activeUnit, p) &&
                    (!isOccupied(state, p) || p.equals(activeUnit.getPosition()))) {

                    if (isAdjacent(p, target.getPosition())) {
                        attackPos = p;
                        break;
                    }
                }
            }
            if (attackPos != null) break;
        }

        if (attackPos != null) return new MoveAndAttackAction(activeUnit, attackPos, target);

        Position bestMovePos = activeUnit.getPosition();
        double minDistance = Double.MAX_VALUE;

        for (int row = 0; row < state.getField().getHeight(); row++) {
            for (int col = 0; col < state.getField().getWidth(); col++) {
                Position p = new Position(col, row);

                if (BattlePathFinder.canReach(state, activeUnit, p) && !isOccupied(state, p)) {
                    double dist = calculateGridDistance(p, target.getPosition());
                    if (dist < minDistance) {
                        minDistance = dist;
                        bestMovePos = p;
                    }
                }
            }
        }

        if (bestMovePos.equals(activeUnit.getPosition()))return new DefendAction(activeUnit);
        return new MoveAction(activeUnit, bestMovePos);
    }

    private UnitStack findNearestEnemy(BattleState state, UnitStack botUnit) {
        Army enemies = state.getPlayerOne().getArmy();
        UnitStack nearest = null;
        double minDist = Double.MAX_VALUE;

        for (UnitStack enemy : enemies.getUnits()) {
            if (enemy.isAlive()) {
                double dist = calculateGridDistance(botUnit.getPosition(), enemy.getPosition());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = enemy;
                }
            }
        }
        return nearest;
    }

    private double calculateGridDistance(Position p1, Position p2) {
        return Math.sqrt(Math.pow(p1.x() - p2.x(), 2) + Math.pow(p1.y() - p2.y(), 2));
    }

    private boolean isAdjacent(Position p1, Position p2) {
        for (Position neighbor : p1.neighbors()) {
            if (neighbor.equals(p2)) return true;
        }
        return false;
    }

    private boolean isOccupied(BattleState state, Position position) {
        return isOccupiedByArmy(position, state.getPlayerOne().getArmy()) ||
            isOccupiedByArmy(position, state.getPlayerTwo().getArmy());
    }

    private boolean isOccupiedByArmy(Position position, Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive() && unit.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }
}
