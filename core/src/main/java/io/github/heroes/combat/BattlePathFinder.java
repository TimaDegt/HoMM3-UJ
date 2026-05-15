package io.github.heroes.combat;

import io.github.heroes.model.Army;
import io.github.heroes.model.BattleField;
import io.github.heroes.model.BattleState;
import io.github.heroes.model.Position;
import io.github.heroes.model.UnitStack;
import io.github.heroes.view.BattlefieldGeometry;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BattlePathFinder {
    private final BattlefieldGeometry battlefieldGeometry;

    public BattlePathFinder(BattlefieldGeometry battlefieldGeometry) {
        if (battlefieldGeometry == null) {
            throw new IllegalArgumentException("Battlefield geometry cannot be null");
        }

        this.battlefieldGeometry = battlefieldGeometry;
    }

    public boolean canReach(BattleState state, UnitStack unit, Position targetPosition) {
        int distance = findDistance(state, unit.getPosition(), targetPosition);
        return distance >= 0 && distance <= unit.getType().speed;
    }

    public int findDistance(BattleState state, Position start, Position target) {
        if (start.equals(target)) {
            return 0;
        }

        BattleField field = state.getField();
        Queue<Position> queue = new ArrayDeque<>();
        Map<Position, Integer> distances = new HashMap<>();
        Set<Position> visited = new HashSet<>();

        queue.add(start);
        distances.put(start, 0);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.remove();
            int currentDistance = distances.get(current);

            for (Position neighbor : battlefieldGeometry.getNeighbors(current)) {
                if (!field.isInside(neighbor) || visited.contains(neighbor)) {
                    continue;
                }
                if (isOccupied(state, neighbor) && !neighbor.equals(target)) {
                    continue;
                }

                int nextDistance = currentDistance + 1;
                if (neighbor.equals(target)) {
                    return nextDistance;
                }

                queue.add(neighbor);
                distances.put(neighbor, nextDistance);
                visited.add(neighbor);
            }
        }

        return -1;
    }

    private boolean isOccupied(BattleState state, Position position) {
        return isOccupiedByArmy(position, state.getPlayerOne().getArmy())
            || isOccupiedByArmy(position, state.getPlayerTwo().getArmy());
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
