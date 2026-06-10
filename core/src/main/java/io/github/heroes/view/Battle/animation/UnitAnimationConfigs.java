package io.github.heroes.view.Battle.animation;

import io.github.heroes.model.state.UnitType;

public final class UnitAnimationConfigs {
    private static final AnimParams PIKEMAN = new AnimParams(
        125, 0, 5, 1, 6, 3, 6, -1, 0, 6, 6, 7, 7, 8, 0, 0
    );
    private static final AnimParams ARCHER = new AnimParams(
        125, 0, 6, 1, 6, 3, 6, 3, 6, 2, 6, 6, 5, 8, 0, 0
    );
    private static final AnimParams GRIFFIN = new AnimParams(
        152, 1, 8, 2, 5, 3, 7, -1, 0, 4, 6, 0, 5, 5, -37, -12
    );

    private UnitAnimationConfigs() {
    }

    public static AnimParams get(UnitType type) {
        if (type == null) {
            throw new IllegalArgumentException("Unit type cannot be null");
        }

        return switch (type) {
            case PIKEMAN -> PIKEMAN;
            case ARCHER -> ARCHER;
            case GRIFFIN -> GRIFFIN;
        };
    }
}
