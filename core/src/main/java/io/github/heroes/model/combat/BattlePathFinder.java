package io.github.heroes.model.combat;

import io.github.heroes.model.state.Army;
import io.github.heroes.model.state.BattleField;
import io.github.heroes.model.state.BattleState;
import io.github.heroes.model.state.Position;
import io.github.heroes.model.state.UnitStack;
import io.github.heroes.view.Battle.BattlefieldGeometry;

import java.util.*;

public class BattlePathFinder {

    public BattlePathFinder() {
    }

    public static boolean canReach(BattleState state, UnitStack unit, Position targetPosition) {
        int distance = findDistance(state, unit.getPosition(), targetPosition);
        return distance >= 0 && distance <= unit.getType().speed;
    }

    public static int findDistance(BattleState state, Position start, Position target) {
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

            Position[] neighbors = BattlefieldGeometry.getNeighbors(current);
            for (Position neighbor : neighbors) {
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


    public static List<Position> findPath(BattleState state, Position start, Position target) {
        if (start.equals(target)) {
            return new ArrayList<>(Collections.singleton(start));
        }

        BattleField field = state.getField();
        Queue<Position> queue = new ArrayDeque<>();
        Map<Position, Position> prevPosition = new HashMap<>();
        Set<Position> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        prevPosition.put(start,start);

        while (!queue.isEmpty()) {
            Position current = queue.remove();

            Position[] neighbors = BattlefieldGeometry.getNeighbors(current);
            for (Position neighbor : neighbors) {
                if (!field.isInside(neighbor) || visited.contains(neighbor)) {
                    continue;
                }
                if (isOccupied(state, neighbor) && !neighbor.equals(target)) {
                    continue;
                }

                prevPosition.put(neighbor,current);
                if (neighbor.equals(target)) {
                    return recreatePath(prevPosition, target);
                }
                queue.add(neighbor);
                visited.add(neighbor);
            }
        }
        throw new IllegalArgumentException("Position can not be reached");
    }

    private static List<Position> recreatePath(Map<Position,Position> prevPosition, Position target){
        List<Position> path = new ArrayList<>(Collections.singleton(target));
        while(!prevPosition.get(target).equals(target)){
            target=prevPosition.get(target);
            path.add(target);
        }
        return path.reversed();
    }

    private static boolean isOccupied(BattleState state, Position position) {
        return isOccupiedByArmy(position, state.getPlayerOne().getArmy())
            || isOccupiedByArmy(position, state.getPlayerTwo().getArmy());
    }

    private static boolean isOccupiedByArmy(Position position, Army army) {
        for (UnitStack unit : army.getUnits()) {
            if (unit.isAlive() && unit.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
    }
}
