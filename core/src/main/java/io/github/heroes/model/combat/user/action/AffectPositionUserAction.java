package io.github.heroes.model.combat.user.action;

import io.github.heroes.model.state.Position;

public class AffectPositionUserAction implements UserAction {
    private final Position affectedPosition;
    private final Position nearestPosition;

    public AffectPositionUserAction(Position affectedPosition, Position nearestPosition) {
        if (affectedPosition == null) throw new IllegalArgumentException("Affected position cannot be null");

        this.affectedPosition=affectedPosition;
        this.nearestPosition=nearestPosition;
    }

    public Position getAffectedPosition() {
        return affectedPosition;
    }

    public Position getNearestPosition() {
        return nearestPosition;
    }
}
