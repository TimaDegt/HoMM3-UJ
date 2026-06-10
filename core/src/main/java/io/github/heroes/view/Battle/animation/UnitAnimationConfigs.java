package io.github.heroes.view.Battle.animation;

import io.github.heroes.model.state.UnitType;

public final class UnitAnimationConfigs {
    private static final AnimParams PIKEMAN = new AnimParams(
        125, 0, 5, 1, 6, 3, 6, -1, 0, 6, 6, 7, 7, 8, 0, 0, false
    );
    private static final AnimParams ARCHER = new AnimParams(
        125, 0, 6, 1, 6, 3, 6, 3, 6, 2, 6, 6, 5, 8, 0, 0, false
    );
    private static final AnimParams GRIFFIN = new AnimParams(
        152, 1, 8, 2, 5, 3, 7, -1, 0, 4, 8, 0, 5, 5, -37, -12, true
    );
    private static final AnimParams CAVALIER = new AnimParams(
        150, 3, 8, 2, 5, 1, 9, -1, 0, 4, 11, 0, 0, 9, -45, 0, false
    );
    private static final AnimParams ANGEL = new AnimParams(
        150, 4, 6, 3, 6, 2, 6, -1, 0, 6, 10, 0, 0, 5, -60, -35, true
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
            case CAVALIER -> CAVALIER;
            case ANGEL -> ANGEL;
        };
    }
}
