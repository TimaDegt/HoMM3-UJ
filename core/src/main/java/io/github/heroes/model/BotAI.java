package io.github.heroes.model;

import io.github.heroes.control.BattleController;
import io.github.heroes.model.combat.*;
import io.github.heroes.model.state.Army;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;

public class BotAI {
    private final BattleController battleController;

    public BotAI(BattleController battleController, BattlePathFinder pathFinder) {
        this.battleController = battleController;
    }

    public BattleAction takeTurn() {
        UnitStack activeUnit = battleController.getActiveUnit();
        if (activeUnit == null || !activeUnit.isAlive()) return null;
        UnitStack target = findNearestEnemy(activeUnit);
        if (target == null) return null;

        BattleState state = battleController.getState();
        Position attackPos = null;

        for (int row = 0; row < state.getField().getHeight(); row++) {
            for (int col = 0; col < state.getField().getWidth(); col++) {
                Position p = new Position(col, row);

                if (BattlePathFinder.canReach(state, activeUnit, p) &&
                    (!isOccupied(p) || p.equals(activeUnit.getPosition()))) {

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

                if (BattlePathFinder.canReach(state, activeUnit, p) && !isOccupied(p)) {
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

    private UnitStack findNearestEnemy(UnitStack botUnit) {
        Army enemies = battleController.getState().getPlayerOne().getArmy();
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
        int[][] evenRowDirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}};
        int[][] oddRowDirs  = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 1}};
        int[][] dirs = (p1.y() % 2 == 0) ? evenRowDirs : oddRowDirs;

        for (int[] dir : dirs) {
            if (p1.x() + dir[0] == p2.x() && p1.y() + dir[1] == p2.y()) {
                return true;
            }
        }
        return false;
    }

    private boolean isOccupied(Position position) {
        return isOccupiedByArmy(position, battleController.getState().getPlayerOne().getArmy()) ||
            isOccupiedByArmy(position, battleController.getState().getPlayerTwo().getArmy());
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
